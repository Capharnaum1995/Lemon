package com.lemon.community.interceptor;

import com.lemon.community.mapper.UserMapper;
import com.lemon.community.model.User;
import com.lemon.community.model.UserExample;
import com.lemon.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {//该类前面需要加上注解，HandlerInterceptor本身不会被spring boot所接管，UserMapper会无法注入。因此需要在该类前面加上@Service注解让spring boot来接管它
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) { //首次登陆的时候cookie可能是空的，如果是空的的话就不做操作
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        User user = users.get(0);
                        Long notificationCount = notificationService.getNotificationCount(user.getId());
                        request.getSession().setAttribute("user", user);
                        request.getSession().setAttribute("notificationCount", notificationCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
