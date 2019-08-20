function comment() {
    var parentId = $("#questionId").val();
    var content = $("#commentContent").val();
    commentToTarget(parentId, content, 1, null, null);
}

function commentToTarget(parentId, content, type, atId, originId) {
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
            "type": type,
            "atId": atId,
            "originId": originId
        }),
        success: function (response) {
            if (response.code == 200) {                             //评论发表成功
                var comment = response.data;                        //从后台获取该commentDTO
                if (comment.type == 1) {                            //1.如果是评论，直接生成评论组件添加
                    var newComment = generateComment(comment)
                    $("#commentContent").val("");
                    $("#comment-container").prepend(newComment);
                } else {                                            //2.如果是回复,生成回复组件
                    var id = comment.parentId;                            //2.1获取comment的parentId，通过该parentId才能够确定生成的新的回复组件往哪个区域添加
                    var subCommentContainer = $("#comment-" + id);        //2.2获取评论对应的二级评论区元素
                    var newReply = generateReply(comment, id);
                    subCommentContainer.prepend(newReply);
                }
                $("#reply-" + parentId).val("");                    //3.回复成功之后清除回复框里面的内容
            } else {
                if (response.code == 2002) {
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

function collapseReplies(e) {
    var id = e.getAttribute("data-commentId");     //获取comment的id
    var subCommentContainer = $("#comment-" + id);//获取评论对应的二级评论区元素
    var collapse = e.getAttribute("data-collapse");//获取二级评论区的展开状态。初次没有添加该状态属性到组件上，获取的时候是为空的
    if (collapse) {
        subCommentContainer.removeClass("in");//移除class，实现不显示
        var children = subCommentContainer.children();//移除组件所有回复标签，避免下次加载时元素重叠
        for (var i = 0; i < children.length; i++) {
            children[i].remove();
        }
        e.removeAttribute("data-collapse")//移除标记
    } else {
        //从后台获取二级评论的数据,先清除之前组件内的所有组件
        var children = subCommentContainer.children();//移除组件所有回复标签，避免下次加载时元素重叠
        for (var i = 0; i < children.length; i++) {
            children[i].remove();
        }
        $.getJSON("/comment/" + id, function (data) {
            $.each(data.data.reverse(), function (index, reply) {
                var newReply = generateReply(reply, id);
                subCommentContainer.prepend(newReply);
            });
        });
        subCommentContainer.addClass("in");//展开二级评论区
        e.setAttribute("data-collapse", "in");//标记二级评论区为展开状态
    }
}

function generateComment(comment) {
    var newComment = "<div class='media'>" +
        "                 <div class='media-left'>" +
        "                     <a href='#'>" +
        "                         <img class='avatar-middle img-circle' src='" + comment.user.avatarUrl + "'>" +
        "                     </a>" +
        "                 </div>" +
        "                 <div class='media-body' id='media-body-" + comment.id + "'>" +
        "                     <h5 class='media-heading'>" +
        "                         <span>" + comment.user.name + "</span>" +
        "                     </h5>" +
        "                     <div>" + comment.content + "</div>" +
        "                     <div>" +
        "                         <span>" + moment(comment.gmtCreate).format('YYYY-MM-DD') + "</span>" +
        "                         <a class='comment-reply' data-id='" + comment.id + "' onclick='replyBox(this)'>回复</a>" +
        "                         <a data-commentId='" + comment.id + "' onclick='collapseReplies(this)' style='float:right;'>" +
        "                             <span class='glyphicon glyphicon-comment comment-like'>" + comment.commentCount + "</span>" +
        "                         </a>" +
        "                     </div>" +
        "                     <div class='col-lg-12 col-ms-12 col-sm-12 col-xs-12 sub-comment collapse' id='comment-" + comment.id + "'></div>" +
        "                 </div>" +
        "                 <hr class='division'>" +
        "             </div>"
    return newComment;
}

function generateReply(reply, id) {
    var var1 = "<div class='media'>" +
        "            <div class='media-left'>" +
        "                <a href='#'>" +
        "                    <img class='avatar-small img-circle' src='" + reply.user.avatarUrl + "'>" +
        "                </a>" +
        "            </div>" +
        "            <div class='media-body'>" +
        "                <h5 class='media-heading'>" +
        "                    <span>" + reply.user.name + "</span>" +
        "                </h5>";
    var var2;
    if (reply.atTarget != null) {
        var2 = "<div>@" + reply.atTarget.name + "：" + reply.content + "</div>";
    } else {
        var2 = "<div>" + reply.content + "</div>";
    }
    var var3 = "         <div>" +
        "                    <span>" + moment(reply.gmtCreate).format('YYYY-MM-DD') + "</span>" +
        "                    <a  class='comment-reply' data-id='" + id + "' data-atid='" + reply.user.id + "' data-atname='" + reply.user.name + "' data-id1='" + reply.id + "' onclick='replyBox(this)'>回复</a>" +
        "                </div>" +
        "            </div>" +
        "            <hr class='division'>" +
        "       </div>";
    var newReply = var1 + var2 + var3;
    return newReply;
}

function replyBox(e) {
    var id = e.getAttribute("data-id");
    var atId = e.getAttribute("data-atid");
    var atName = e.getAttribute("data-atname");
    var originId = e.getAttribute("data-id1");
    var placeholder = "";
    if (atId == null) {
        placeholder = "Reply...";
    } else {
        placeholder = "@" + atName + "：";
    }
    var replyArea = $(".reply-area");                                            //获取回复框组件
    if (replyArea.length == 0) {                                                 //1.该组件不存在-->在对应的评论下创建该组件
        var mediaBody = $("#media-body-" + id);
        var replyBox = generateReplyBox(id, atId, originId, placeholder);
        mediaBody.append(replyBox);
    } else {                                                                    //2.回复框已存在
        var replySubmitBtn = $("#reply-submit-btn");
        var id1 = replySubmitBtn.data("id");
        var atId1 = replySubmitBtn.data("atid");
        if (id == id1) {                                                           //2.1 回复框是同一个评论下的
            if (atId == null && atId1 == null) {      //2.1.1 两次点击的都是评论后面的回复按钮
                replyArea.remove();
            } else {                                  //2.1.2 其他的情况，只需要改变reply-submit-btn的data-atId和placeholder
                $(".reply-content").attr("placeholder", placeholder);
                $("#reply-submit-btn").data("atid", atId);
                $("#reply-submit-btn").data("originid", originId);
            }
        } else {                                                                   //2.2 回复框不是同一个评论下面的
            replyArea.remove();
            var mediaBody = $("#media-body-" + id);
            var replyBox = generateReplyBox(id, atId, originId, placeholder);
            mediaBody.append(replyBox);
        }
    }
}

function generateReplyBox(id, atId, originId, placeholder) {
    var replyBox = "<div class='col-lg-12 col-ms-12 col-sm-12 col-xs-12 reply-area'>" +
        "               <div class='media'>" +
        "                   <div class='media-left'>" +
        "                       <img class='avatar-middle img-circle' src='" + sessionStorage.getItem("avatarUrl") + "'>" +
        "                   </div>" +
        "                   <div class='media-body reply-input'>" +
        "                       <div class='input-group'>" +
        "                           <input type='text' class='form-control reply-content' placeholder='" + placeholder + "' id='reply-" + id + "'>" +
        "                           <span class='input-group-btn'>" +
        "                               <button id='reply-submit-btn' class='btn btn-default' type='button' data-id='" + id + "' data-atid='" + atId + "' data-originid='" + originId + "' onclick='reply(this)'>回复</button>" +
        "                           </span>" +
        "                       </div>" +
        "                   </div>" +
        "               </div>" +
        "           </div>";
    return replyBox;
}

function reply(e) {
    var parentId = e.getAttribute("data-id");         //获取回复按钮组件上绑定的parentId
    var atId = e.getAttribute("data-atid");           //获取被艾特人的id
    var originId = e.getAttribute("data-originid");
    var content = $("#reply-" + parentId).val();      //根据parentId拼接成id选择器的名称，从而获取对应input组件内的回复内容
    commentToTarget(parentId, content, 2, atId, originId);
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