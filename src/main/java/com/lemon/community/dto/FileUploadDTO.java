package com.lemon.community.dto;

import lombok.Data;

@Data
public class FileUploadDTO {
    private Integer success;            // 0 表示上传失败，1 表示上传成功
    private String message;     // 提示的信息，上传成功或上传失败及错误信息等
    private String url;          // 图片上传后的地址,上传成功时才返回
}
