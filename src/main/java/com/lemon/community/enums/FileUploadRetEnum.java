package com.lemon.community.enums;

public enum FileUploadRetEnum {
    FILE_UPLOAD_SUCCESS(1, "文件上传成功！"),
    FILE_UPLOAD_FAILED(0, "文件上传失败！");
    private Integer result;
    private String message;

    public Integer getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    FileUploadRetEnum(Integer result, String message) {
        this.result = result;
        this.message = message;
    }
}
