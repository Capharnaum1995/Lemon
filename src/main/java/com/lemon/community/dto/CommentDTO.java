package com.lemon.community.dto;

import com.lemon.community.model.User;
import lombok.Data;

/**
 * 返回到页面的评论DTO对象
 */
@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long commentCount;
    private Long likeCount;
    private String content;
    private User user;          //comment的创建者
    private User atTarget;      //comment的艾特对象，optional
}
