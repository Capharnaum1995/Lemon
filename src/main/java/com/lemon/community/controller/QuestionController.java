package com.lemon.community.controller;

import com.lemon.community.dto.CommentDTO;
import com.lemon.community.dto.QuestionDTO;
import com.lemon.community.enums.CommentTypeEnum;
import com.lemon.community.model.User;
import com.lemon.community.service.CommentService;
import com.lemon.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {
        questionService.increaseViewCount(id);//这里采取：点击了问题，先增加其浏览数，然后再获取问题。显示比较及时
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        List<QuestionDTO> relatedQuestions = questionService.getRelatedQuestion(questionDTO);
        List<CommentDTO> commentDTOS = commentService.getCommentListByPId(id, CommentTypeEnum.COMMENT);
        //将数据传给页面
        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }

    @ResponseBody
    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public String getAvatarUrl(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            return user.getAvatarUrl();
        } else {
            return "http://capharnaum.cn-bj.ufileos.com/49dcbced-2bbe-447a-953e-b17b15404597.jpg?UCloudPublicKey=TOKEN_1adcc88c-bd96-4c1e-896e-660e517603ef&Signature=GCPSXVujwUvHGukMJ%2Ft2vGgKVyg%3D&Expires=1881607877";
        }
    }
}
