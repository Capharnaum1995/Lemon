package com.lemon.community.mapper;

import com.lemon.community.model.Question;

import java.util.List;

public interface QuestionMapperExt {
    int increaseViewCount(Question question);

    int increaseCommentCount(Question question);

    List<Question> selectRelatedQuestion(Question question);
}