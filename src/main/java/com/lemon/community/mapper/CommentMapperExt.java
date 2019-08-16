package com.lemon.community.mapper;

import com.lemon.community.model.Comment;

public interface CommentMapperExt {
    int increaseCommentCount(Comment record);

    int increaseLikeCount(Comment record);

    /**
     * CommentMapperExt对象调用该方法进行插入一条记录，可以获取该记录的主键值：
     * Comment对象：comment
     * commentMapper.insertAndGetPrimaryKey(comment);
     * id=comment.getId();
     *
     * @param record
     * @return
     */
    int insertAndGetPrimaryKey(Comment record);
}
