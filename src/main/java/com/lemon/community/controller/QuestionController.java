package com.lemon.community.controller;

import com.lemon.community.dto.CommentDTO;
import com.lemon.community.dto.QuestionDTO;
import com.lemon.community.enums.CommentTypeEnum;
import com.lemon.community.service.CommentService;
import com.lemon.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        List<CommentDTO> commentDTOS = commentService.getCommentListByPId(id, CommentTypeEnum.QUESTION);
        //将数据传给页面
        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
