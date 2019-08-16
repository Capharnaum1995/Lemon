package com.lemon.community.enums;

public enum NotificationTypeEnum {
    COMMENT(1, "评论了你的问题"),
    REPLY(2, "回复了你的评论");
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
