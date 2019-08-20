package com.lemon.community.enums;

public enum CommentTypeEnum {
    //评论的类型，如果是1的话代表的是一级评论（问题的直接评论）;如果是2的话代表的是二级评论（也就是评论的评论）
    COMMENT(1),
    REPLY(2);
    private Integer type;

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {//枚举的遍历方式
            if (commentTypeEnum.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }
}
