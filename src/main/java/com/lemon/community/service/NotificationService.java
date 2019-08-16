package com.lemon.community.service;

import com.lemon.community.dto.NotificationDTO;
import com.lemon.community.dto.PagingDTO;
import com.lemon.community.enums.NotificationStatusEnum;
import com.lemon.community.enums.NotificationTypeEnum;
import com.lemon.community.mapper.CommentMapper;
import com.lemon.community.mapper.NotificationMapper;
import com.lemon.community.mapper.QuestionMapper;
import com.lemon.community.mapper.UserMapper;
import com.lemon.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 初始化个人消息页面
     *
     * @param pageSize
     * @param pageNow
     * @param id
     * @return
     */
    public PagingDTO initPage(Integer pageSize, Integer pageNow, Long id) {
        PagingDTO<NotificationDTO> pagingDTO = new PagingDTO<>();
        //计算出分页数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UN_READ.getStatus());
        Integer pageCount = ((int) notificationMapper.countByExample(notificationExample) - 1) / pageSize + 1;

        //防止用户在地址栏手动输入
        if (pageNow > pageCount) {
            pageNow = pageCount;
        }
        if (pageNow < 1) {
            pageNow = 1;
        }

        //根据pageNow获得所有通知对象
        Integer offset = (pageNow - 1) * pageSize;
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, pageSize));
        if (notifications.size() == 0) {
            return pagingDTO;
        }

        //notifications-->notificationDTOS
        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            User commenter = userMapper.selectByPrimaryKey(notification.getCommenter());
            Long gmtCreate = notification.getGmtCreate();
            String content = notification.getContent();
            Integer notifyType = null;
            String originContent = null;
            Long id1 = null;
            if (notification.getType() == NotificationTypeEnum.COMMENT.getType()) {
                notifyType = NotificationTypeEnum.COMMENT.getType();
                Question question = questionMapper.selectByPrimaryKey(notification.getOriginId());
                originContent = question.getTitle();
                id1 = question.getId();
            } else {
                notifyType = NotificationTypeEnum.REPLY.getType();
                Comment comment = commentMapper.selectByPrimaryKey(notification.getOriginId());
                originContent = comment.getContent();
                Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
                id1 = question.getId();
            }
            notificationDTO.setCommenter(commenter);
            notificationDTO.setGmtCreate(gmtCreate);
            notificationDTO.setContent(content);
            notificationDTO.setNotifyType(notifyType);
            notificationDTO.setOriginContent(originContent);
            notificationDTO.setId(id1);
            return notificationDTO;
        }).collect(Collectors.toList());

        //notificationDTOS-->pagingDTO
        pagingDTO.setData(notificationDTOS);
        pagingDTO.initPage(pageCount, pageNow);
        return pagingDTO;
    }

    /**
     * 根据用户id获取用户未读消息数量
     *
     * @param id
     * @return
     */
    public Long getNotificationCount(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UN_READ.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

}
