function comment() {
    var questionId = $("#questionId").val();
    var content = $("#commentContent").val();
    commentToTarget(questionId, content, 1);
}

function reply(e) {
    var commentId = e.getAttribute("data-id");//获取回复按钮组件上绑定的评论的id
    var content = $("#reply-" + commentId).val();//根据id拼接成id选择器的名称，从而获取对应input组件内的回复内容
    commentToTarget(commentId, content, 2);
}

function commentToTarget(parentId, content, type) {
    if (!content) {//前端的验证
        alert("评论的内容不能为空!");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({//需要将js的对象转化为JSON对象，使用JSON.stringify()方法
            "parentId": parentId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {                             //评论发表成功
                var comment = response.data;                       //从后台获取该commentDTO
                var newComment = "<div class='media'>" +
                    "                        <div class='media-left'>" +
                    "                            <a href='#'>" +
                    "                                <img class='avatar-small img-circle' src='" + comment.user.avatarUrl + "'>" +
                    "                            </a>" +
                    "                        </div>" +
                    "                        <div class='media-body'>" +
                    "                            <h5 class='media-heading'>" +
                    "                                <span>" + comment.user.name + "</span>" +
                    "                            </h5>" +
                    "                            <div>" + comment.content + "</div>" +
                    "                            <div>" +
                    "                                <button type='button' class='btn btn-default btn-xs comment-button'><span class='glyphicon glyphicon-thumbs-up comment-like'></span></button>" +
                    "                                <button type='button' class='btn btn-default btn-xs comment-button' data-id='" + comment.id + "' onclick='collapseComments(this)'>" +
                    "                                    <span class='glyphicon glyphicon-comment comment-like'>" + comment.commentCount + "</span>" +
                    "                                </button>" +
                    "                                <span style='float: right;'>" + moment(comment.gmtCreate).format('YYYY-MM-DD') + "</span>" +
                    "                            </div>" +
                    "                            <div class='col-lg-12 col-ms-12 col-sm-12 col-xs-12 sub-comment collapse' id='comment-" + comment.id + "'>" +
                    "                                <div class='row'>" +
                    "                                    <div class='sub-comment-box'>" +
                    "                                        <div class='input-group'>" +
                    "                                            <input type='text' class='form-control' placeholder='Reply...' id='reply-" + comment.id + "'>" +
                    "                                            <span class='input-group-btn'><button class='btn btn-default' type='button' data-id='" + comment.id + "' onclick='reply(this)'>回复</button></span>" +
                    "                                        </div>" +
                    "                                    </div>" +
                    "                                </div>" +
                    "                            </div>" +
                    "                        </div>" +
                    "                        <hr class='division'>" +
                    "                    </div>"
                $("#commentContent").val("");
                $("#comment-container").prepend(newComment);
            } else {
                if (response.code == 2001) {
                    var confirmLogin = confirm(response.message);
                    if (confirmLogin) {
                        window.open("https://github.com/login/oauth/authorize?client_id=751da1a087d0187c3619&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("isClose", "yes");
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var subCommentContainer = $("#comment-" + id);//获取二级评论区组件元素
    var collapse = e.getAttribute("data-collapse");//获取二级评论区的展开状态。初次没有添加该状态属性到组件上，获取的时候是为空的
    if (collapse) {
        subCommentContainer.removeClass("in");//移除class，实现不显示
        var children = subCommentContainer.children();//移除组件所有回复标签，避免下次加载时元素重叠
        for (var i = 0; i < children.length - 1; i++) {
            children[i].remove();
        }
        e.removeAttribute("data-collapse")//移除标记
    } else {
        //从后台获取二级评论的数据,先清除之前组件内的所有组件
        $.getJSON("/comment/" + id, function (data) {
            $.each(data.data.reverse(), function (index, comment) {
                var media = $("<div/>", {
                    "class": "media"
                });
                var media_left = $("<div/>", {
                    "class": "media-left"
                });
                var media_body = $("<div/>", {
                    "class": "media-body"
                });
                var division = $("<hr/>", {
                    "class": "division"
                });
                subCommentContainer.prepend(media);
                media.append(media_left);
                media.append(media_body);
                media.append(division);

                var avatar = $("<a/>", {
                    "href": "#"
                });
                var img = $("<img/>", {
                    "class": "avatar-small img-circle",
                    "src": comment.user.avatarUrl
                });
                avatar.append(img);
                media_left.append(avatar);

                var h5 = $("<h5/>", {
                    "class": "media-heading"
                });
                var span = $("<span/>", {
                    "text": comment.user.name
                });
                h5.append(span);
                media_body.append(h5);

                var content = $("<div/>", {
                    "text": comment.content
                });
                var time = $("<div/>", {
                    "text": moment(comment.gmtCreate).format('YYYY-MM-DD')
                });
                media_body.append(content);
                media_body.append(time);
            });
        });
        subCommentContainer.addClass("in");//展开二级评论区
        e.setAttribute("data-collapse", "in");//标记二级评论区为展开状态
    }
}

/**
 * 标签库的控制函数
 */
$(function () {
    //初始化li标签，为其添加对应的id
    $(function () {
        var id;
        $(".label-item li").each(function () {
            id = $(this).children("span").attr("tag-id");
            $(this).attr("tag-id", id);
            $(this).children("span").removeAttr("tag-id");
        });
    });

    //还原页面的标签
    $(function () {
        var var0 = $(".tag-selected");
        var var1 = $("input[name='tag']").val();        //var1="/1/2/3/"
        if (var1 != null && var1 != '') {
            var var2 = var1.substring(1, var1.length - 1);  //var2="1/2/3"
            var var3 = var2.split("/");            //var3=["1", "2", "3"]
            var text, labelHTML;
            for (var i = 0; i < var3.length; i++) {
                text = $(".label-item").find("li[tag-id='" + var3[i] + "']").children("span").html();
                labelHTML = "<li tag-id='" + var3[i] + "'><span class='label label-info select-tag'>x" + text + "</span></li>";
                var0.append(labelHTML);
                $(".label-item").find("li[tag-id='" + var3[i] + "']").addClass("selected");
                $(".label-item").find("li[tag-id='" + var3[i] + "']").children().removeClass("label-info");
                $(".label-item").find("li[tag-id='" + var3[i] + "']").children().addClass("tag-validate");
            }
        }
    });

    //显示标签框监听函数
    $(".show-labelitem").on("click", function () {
        $(this).hide();                //“显示按钮”隐藏
        $(".hide-labelitem").show();   //“隐藏按钮”显示
        $("#labelItem").show();        //“标签选择框”显示
    });

    //隐藏标签框监听函数
    $(".hide-labelitem").on("click", function () {
        $(this).hide();
        $(".show-labelitem").show();
        $("#labelItem").hide();
    });

    //选择标签
    $(".label-item").on("click", "li", function () {
        var id = $(this).attr("tag-id");
        var text = $(this).children("span").html();
        //.html()方法是获取标签的html代码
        var labelHTML = "<li tag-id='" + id + "'><span class='label label-info select-tag'>x" + text + "</span></li>";//根据得到的标签id以及标签的内容拼接成新的标签
        if ($(this).hasClass("selected")) {                             //如果当前标签已经被选中过则不进行任何操作
            return false;
        } else if ($(".tag-selected").children("li").length >= 8) {        //如果当前标签未被选中，但已被选中的标签个数已达8个，则弹出警告
            window.alert("标签最多只可以选择8个哦！")
            return false;
        }
        //还可以添加标签的情况
        $(".tag-selected").append(labelHTML);                            //将选择后重新拼接的标签元素追加在框内
        val = '/';                                                       //将标签框内的标签id拼接成：‘/1/2/3/’的格式
        for (var i = 0; i < $(".tag-selected").children("li").length; i++) {
            val += $(".tag-selected").children("li").eq(i).attr("tag-id") + '/';
        }
        $("input[name='tag']").val(val);                               //将拼接好的标签id串放入input中
        $(this).addClass("selected");                               //标记当前标签为已选择
        var var1 = $(this).children();                                 //已被选择的标签颜色设为浅色
        var1.removeClass("label-info");
        var1.addClass("tag-validate");
    });

    //取消标签
    var val = "";
    $(".tag-selected").on("click", "li", function () {
        var id = $(this).attr("tag-id");
        $(this).remove();
        if ($(".tag-selected").children("li").length == 0) {
            $("input[name='tag']").val('');
        } else {
            val = '/';
            for (var i = 0; i < $(".tag-selected").children("li").length; i++) {
                val += $(".tag-selected").children("li").eq(i).attr("tag-id") + '/';
            }
            $("input[name='tag']").val(val);
        }
        $(".label-item").find("li[tag-id='" + id + "']").removeClass("selected");
        var var1 = $(".label-item").find("li[tag-id='" + id + "']").children();//标签恢复为原来的颜色
        var1.removeClass("tag-validate");
        var1.addClass("label-info");
    });
    /**
     //点击更换标签
     var limit = 0;
     $(".replacelable").on("click", function () {
                            layer.load(1);
                            limit += 32;
                            $.ajax({
                                url: window.location.href,
                                type: "post",
                                datatype: "json",
                                data: {
                                    labellimit: limit
                                },
                                success: function (data) {
                                    layer.closeAll('loading');
                                    $(".label-item").find("li").remove();//删除原先所有，本来想让数据表随机搜索的，想着有点mmp，就没搞，用的是limit
                                    var html = '';
                                    for (var i in data) {
                                        html += "<li data=\"" + data[i].term_id + "\">x<span>" + data[i].name + "</span></li>";//拼接标签
                                    }
                                    $(".label-item").append(html);//追加至文档
                                },
                                error: function () {
                                    layer.closeAll('loading');
                                    layer.msg("错误~~~");
                                }
                            })
                        });
     //获取标签
     $("#cell").on("click", function () {
                            if ($("input[name='label']").val() == "") {
                                return false;
                            } else {
                                layer.msg("标签内容为：" + $("input[name='label']").val());
                            }
                        });
     */
});