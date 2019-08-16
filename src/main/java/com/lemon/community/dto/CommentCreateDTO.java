package com.lemon.community.dto;

import lombok.Data;

/**
 * 从页面接收评论对象的DTO类
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
