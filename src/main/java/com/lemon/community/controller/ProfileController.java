package com.lemon.community.controller;

import com.lemon.community.dto.PagingDTO;
import com.lemon.community.model.User;
import com.lemon.community.service.NotificationService;
import com.lemon.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize,
                          @RequestParam(name = "pageNow", defaultValue = "1") Integer pageNow,
                          HttpServletRequest request,
                          Model model) {
        //先验证一下看看有没有登录：通过session的方式验证，不用再查数据库验证。若没有登录则跳转回首页面
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        Long id = user.getId();

        //根据不同的action来初始化profile.html的页面信息
        if ("my-questions".equals(action)) {
            PagingDTO pagingDTO = questionService.initPage(pageSize, pageNow, id);
            model.addAttribute("pagingDTO", pagingDTO);
            model.addAttribute("section", "my-questions");
            model.addAttribute("sectionName", "我的提问");
        } else if ("my-replies".equals(action)) {
            PagingDTO pagingDTO = notificationService.initPage(pageSize, pageNow, id);
            model.addAttribute("pagingDTO", pagingDTO);
            model.addAttribute("section", "my-replies");
            model.addAttribute("sectionName", "最新回复");
        }
        return "profile";
    }
}
