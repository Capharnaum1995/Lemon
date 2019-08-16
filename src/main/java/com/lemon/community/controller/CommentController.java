package com.lemon.community.controller;

import com.lemon.community.dto.CommentCreateDTO;
import com.lemon.community.dto.CommentDTO;
import com.lemon.community.dto.ResultDTO;
import com.lemon.community.enums.CommentTypeEnum;
import com.lemon.community.exception.CustomizeErrorCode;
import com.lemon.community.mapper.CommentMapper;
import com.lemon.community.model.Comment;
import com.lemon.community.model.User;
import com.lemon.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 使用@RequestBody就可以将接收到的json格式的的数据准转化对象
 * 该例中，使用@ResponseBody注解可以将返回的对象转化为json格式的“东西”发送到前端。这样用起来就很方便了
 */
@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object doComment(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        //验证工作
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {//验证是否已经登录（登录后才能回复）,如果是未登陆的话返回前端一个错误的状态码
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {//后端的校验，前端也需要做校验
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_FORMAT_WRONG);
        }
        //插入数据库
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentCreateDTO, comment);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        Long id = commentService.insert(comment);  //插入数据库，成功后返回该条记录的主键
        //需要获取到这个评论对象
        comment=commentMapper.selectByPrimaryKey(id);
        CommentDTO commentDTO=new CommentDTO();
        BeanUtils.copyProperties(comment,commentDTO);
        commentDTO.setUser(user);
        return ResultDTO.okOf(commentDTO);
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comment(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.getCommentListByPId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
