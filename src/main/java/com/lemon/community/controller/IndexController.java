package com.lemon.community.controller;

import com.lemon.community.dto.PagingDTO;
import com.lemon.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize,
                        @RequestParam(name = "pageNow", defaultValue = "1") Integer pageNow,
                        Model model) {
        PagingDTO pagingDTO = questionService.initPage(pageSize, pageNow);
        model.addAttribute("pagingDTO", pagingDTO);
        return "index";
    }
}