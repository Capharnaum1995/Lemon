package com.lemon.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001, "o_o 你要找的问题好像已经不在了..."),
    NOT_LOGIN(2002, "o_o 发表评论失败，原因：未登录。"),
    PARENT_COMMENT_LOST(2003, "o_o 评论失败，原因：parentComment丢失。"),
    COMMENT_TYPE_WRONG(2004, "o_o 评论失败，原因：type错误或不存在。"),
    PARENT_COMMENT_NOT_FOUND(2005, "o_o 评论失败，原因：该评论已被删除，请刷新页面"),
    COMMENT_FORMAT_WRONG(2006, "回复内容的格式有误！"),
    SYSTEM_ERROR(2009, "o_o 服务器冒烟了，稍后再试一下吧！");

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {//私有的构造器
        this.code = code;
        this.message = message;
    }
}
