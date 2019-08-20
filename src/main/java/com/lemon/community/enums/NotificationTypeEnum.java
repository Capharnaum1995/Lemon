package com.lemon.community.enums;

public enum NotificationTypeEnum {
    COMMENT(1, "评论了我的问题"),
    REPLY(2, "回复了我的评论"),
    AT(3, "@了我");
    private Integer type;
    private String actionName;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return actionName;
    }

    NotificationTypeEnum(Integer type, String actionName) {
        this.type = type;
        this.actionName = actionName;
    }
}
