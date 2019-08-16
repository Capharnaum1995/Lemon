package com.lemon.community.enums;

public enum NotificationStatusEnum {
    UN_READ(0),
    HAVE_READ(1);
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    NotificationStatusEnum(Integer status) {
        this.status = status;
    }
}
