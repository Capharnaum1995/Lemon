package com.lemon.community.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 跳转到错误的页面。
 * ErrorController可以处理所有的异常。前面通过CustomizeExceptionHandler已经处理了一些已知的错误异常，并将这些已知的错误和异常信息返回页面给用户，
 * 但是有时候还是有一些没有预料到的4xx，5xx的异常我们还没有处理，此时留给该Controller进行处理，同时页面跳转。
 * 取代默认的异常处理，只需要继承ErrorController
 * 可以参照BasicErrorController类的写法
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {
    @Override
    //Returns the path of the error page.
    public String getErrorPath() {
        return "error";
    }

    @RequestMapping(produces = {"text/html"})
    public ModelAndView errorHtml(HttpServletRequest request,
                                  Model model) {
        HttpStatus status = this.getStatus(request);
        if (status.is4xxClientError()) {//是4xx的问题,客户端的问题
            model.addAttribute("message", "*****您的客户端出现异常，要不换个姿势？");
        }
        if (status.is5xxServerError()) {//5xx的问题，服务端出现的问题
            model.addAttribute("message", "*****服务器冒烟了，我们会尽快解决哒...");
        }
        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}
