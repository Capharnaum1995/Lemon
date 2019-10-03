package com.lemon.community.advice;

import com.alibaba.fastjson.JSON;
import com.lemon.community.dto.ResultDTO;
import com.lemon.community.exception.CustomizeErrorCode;
import com.lemon.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 使用@ControllerAdvice和@ExceptionHandler配合处理全局异常
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {//捕获到异常的处理方法
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {//如果返回的是json对象，则说明当前页面的异常我们希望不要跳转，只在当前页面弹出错误提示框
            ResultDTO resultDTO;
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);//显示的抛出的异常
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);//未知的异常
            }

            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter out = response.getWriter();
                out.write(JSON.toJSONString(resultDTO));
                out.close();
            } catch (IOException ex) {
            }
            return null;
        } else {//返回的数据类型不是json对象，我们选择做页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
