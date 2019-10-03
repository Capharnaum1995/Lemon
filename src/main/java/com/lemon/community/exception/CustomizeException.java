package com.lemon.community.exception;

/**
 * 自定义异常类，为了能将异常抛出因此继承运行时异常RuntimeException
 */
public class CustomizeException extends RuntimeException {
    private Integer code;
    private String message;

    public CustomizeException(ICustomizeErrorCode iCustomizeErrorCode) {
        this.code = iCustomizeErrorCode.getCode();
        this.message = iCustomizeErrorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
