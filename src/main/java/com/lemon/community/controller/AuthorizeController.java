package com.lemon.community.controller;

import com.lemon.community.dto.AccessTokenDTO;
import com.lemon.community.dto.GithubUser;
import com.lemon.community.provider.GithubProvider;
import com.lemon.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;//这里加上注解@Autowired就可以将已经实例化的对象放在里面

    @Autowired
    private UserService userService;

    @Value("${github.client.id}")//@Value注解可以从application配置文件中按“键-值”的方法读取信息
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response
    ) {//授权成功后我们需要接收code和state到这两个参数,code十分钟后失效
        //创建accessTokenDTO对象，并设置它的属性值
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setCode(code);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);//传过去AccessTokenDTO对象得到token
        GithubUser githubUser = githubProvider.getUser(accessToken);//再根据token得到用户对象API
        if (githubUser != null) {
            String token = userService.createOrUpdateUser(githubUser);
            //登陆成功写cookie到本地:cookie存放用户的token，同时数据库中也存放了用户的token，这样在重启服务器后，用户的登陆状态也不会消失
            //因为之前我们是将token存放在session中的，重启服务器后session就消亡了，通过之前的session id就获取不到登陆状态了
            response.addCookie(new Cookie("token", token));
            return "redirect:/";
        } else {
            //登陆失败，追加日志，重新登录
            log.error("callback get GitHub error,{}", githubUser);
            return "redirect:/";
        }

    }

    //安全退出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        //移除session域中的user
        request.getSession().removeAttribute("user");
        //删除本地的cookie:删除cookie的方式比较特别，就是创建一个同名的cookie,设置值为null生命周期为零，在写入覆盖就可以了。
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
