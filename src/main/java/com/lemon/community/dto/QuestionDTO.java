package com.lemon.community.dto;

import com.lemon.community.model.Tag;
import com.lemon.community.model.User;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {//该类相当于做question表和user表做链接得到的结果。这个类对作用在QuestionService
    private Long id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Long commentCount;
    private Long viewCount;
    private Long likeCount;
    private List<Tag> tags;//问题的标签数组
    private User user;
}
