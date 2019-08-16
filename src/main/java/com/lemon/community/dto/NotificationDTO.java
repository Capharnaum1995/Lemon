package com.lemon.community.dto;

import com.lemon.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private User commenter;             //评论人
    private Long gmtCreate;             //评论发表的时间
    private String content;             //通知的具体内容
    private Integer notifyType;         //通知的形式，1：“评论了我的问题”；2：“回复了我的评论”
    private String originContent;       //源内容
    private Long id;                    //“进入页面的”id,也就是question的id
}
