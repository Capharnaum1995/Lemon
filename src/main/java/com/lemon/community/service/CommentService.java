package com.lemon.community.service;

import com.lemon.community.dto.CommentDTO;
import com.lemon.community.enums.CommentTypeEnum;
import com.lemon.community.enums.NotificationStatusEnum;
import com.lemon.community.enums.NotificationTypeEnum;
import com.lemon.community.exception.CustomizeErrorCode;
import com.lemon.community.exception.CustomizeException;
import com.lemon.community.mapper.*;
import com.lemon.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentMapperExt commentMapperExt;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionMapperExt questionMapperExt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 评论的插入需要校验评论的完整性:parentId是否存在/type是否正确/是否登录（CommentController中已完成）,若存在异常的的话返回前台相应的错误信息。检验完成后再进行数据表插入操作。
     * 如果是问题的评论还需要进行问题评论数的增加。该方法是一个事务，对数据库的操作有两次，通过@Transactional注解可以将该方法添加为事务。
     * 插入一条Comment记录，同时返回该条记录的主键值
     *
     * @param comment
     * @return
     */
    @Transactional
    public Long insert(Comment comment) {
        Long commentId;
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.PARENT_COMMENT_LOST);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.COMMENT_TYPE_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.QUESTION.getType()) {
            //评论
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            } else {
                commentMapperExt.insertAndGetPrimaryKey(comment);
                commentId = comment.getId();
                question.setCommentCount(1L);
                questionMapperExt.increaseCommentCount(question);
                //进行消息通知的逻辑处理
                createNotification(comment, question.getCreator(), question.getId(), NotificationTypeEnum.COMMENT);
            }
        } else {
            //回复
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());//获取父评论(你回复的时候前端显示父评论还存在，但可能在你“提交评论”之前父评论已经删除了，这里需要再做一次验证。)
            if (parentComment == null) {
                throw new CustomizeException(CustomizeErrorCode.PARENT_COMMENT_NOT_FOUND);
            } else {
                commentMapperExt.insertAndGetPrimaryKey(comment);
                commentId = comment.getId();
                parentComment.setCommentCount(1L);
                commentMapperExt.increaseCommentCount(parentComment);
                //进行消息通知的逻辑处理
                createNotification(comment, parentComment.getCommentator(), parentComment.getId(), NotificationTypeEnum.REPLY);
            }
        }
        return commentId;
    }

    /**
     * 创建通知并插入数据库
     *
     * @param comment  comment对象
     * @param receiver 消息的接收者
     */
    private void createNotification(Comment comment, Long receiver, Long originId, NotificationTypeEnum notificationTypeEnum) {
        Notification notification = new Notification();
        notification.setCommenter(comment.getCommentator());                    //设置通知的发送者
        notification.setReceiver(receiver);                                     //设置通知的接收者
        notification.setOriginId(originId);                                     //设置通知的源id
        notification.setType(notificationTypeEnum.getType());                   //设置通知的类型
        notification.setContent(comment.getContent());                          //设置通知的内容
        notification.setStatus(NotificationStatusEnum.UN_READ.getStatus());     //设置消息为未读状态
        notification.setGmtCreate(comment.getGmtCreate());                      //设置通知的创建时间
        notificationMapper.insertSelective(notification);                       //将通知添加进数据库
    }

    /**
     * 根据parentId以及CommentTypeEnum枚举获取对应的评论列表
     *
     * @param id
     * @param commentTypeEnum
     * @return
     */
    public List<CommentDTO> getCommentListByPId(Long id, CommentTypeEnum commentTypeEnum) {
        //1.根据问题的id获取所有一级评论
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(commentTypeEnum.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //2.获取commentList中的所有creator的id，然后再去重
        Set<Long> userIdSet = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIdList = new ArrayList<>();
        userIdList.addAll(userIdSet);
        //3.根据去重的id获取所有用户对象，并转换为Map<id,user>的形式
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIdList);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //将comment和对应的user对象封装成commentDTO对象，
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
