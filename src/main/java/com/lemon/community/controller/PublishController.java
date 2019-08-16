package com.lemon.community.controller;

import com.lemon.community.dto.QuestionDTO;
import com.lemon.community.dto.TagDTO;
import com.lemon.community.model.Question;
import com.lemon.community.model.User;
import com.lemon.community.service.QuestionService;
import com.lemon.community.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private TagService tagService;

    /**
     * 点击编辑按钮，进入编辑页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        TagDTO tagDTO = tagService.getTagLibrary();
        String tagString = questionService.getTagString(questionDTO);
        model.addAttribute("tagDTO", tagDTO);
        model.addAttribute("title", questionDTO.getTitle());
        model.addAttribute("description", questionDTO.getDescription());
        model.addAttribute("tag", tagString);
        model.addAttribute("id", questionDTO.getId());
        return "/publish";
    }

    /**
     * 点击发布按钮，跳转到发布页面
     *
     * @return
     */
    @GetMapping("/publish")
    public String publish(Model model) {
        TagDTO tagDTO = tagService.getTagLibrary();
        model.addAttribute("tagDTO", tagDTO);
        return "publish";
    }

    /**
     * 发布问题
     *
     * @param title
     * @param description
     * @param tag
     * @param id
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model) {
        //用model存放用户填写的信息，当用户填写问题信息提交未成功，跳回原页面的时候，实现用户原来填写的信息也存在
        TagDTO tagDTO = tagService.getTagLibrary();
        model.addAttribute("tagDTO", tagDTO);
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        //验证填写的信息不能为空，本来应该是前端来做的，但是我们暂且放在后端来实现
        if (title == null || title == "") {
            model.addAttribute("error", "问题的标题不能为空！");
            return "publish";
        }
        if (description == null || description == "") {
            model.addAttribute("error", "问题的描述不能为空！");
            return "publish";
        }
        if (tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空！");
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {//发表失败，返回原页面
            model.addAttribute("error", "用户未登录！");
            return "publish";
        }

        Question question = new Question();
        question.setId(id);//id可能为空
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
