package com.lemon.community.service;

import com.lemon.community.dto.GithubUser;
import com.lemon.community.mapper.UserMapper;
import com.lemon.community.model.User;
import com.lemon.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    //登陆时有两种情况：1.用户已经登陆过。2.用户是第一次登录
    //根据登陆时得到的GithubUser对象来决定重新创建用户还是更新已有用户信息，然后再返回新的token
    public String createOrUpdateUser(GithubUser githubUser) {
        String token = UUID.randomUUID().toString();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(String.valueOf(githubUser.getId()));
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() != 0) {
            //更新用户：新创建一个用户，只需要设置其需要更新的属性即可,因此使用selective的方式更新
            User user = new User();
            user.setName(githubUser.getName());
            user.setToken(token);
            user.setGmtModified(System.currentTimeMillis());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            UserExample userExample1 = new UserExample();
            userExample1.createCriteria().andAccountIdEqualTo(users.get(0).getAccountId());
            userMapper.updateByExampleSelective(user, userExample1);
        } else {
            //增加新用户：直接创建用户的所有信息（除了主键），所以可以使用insert
            User user = new User();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);
        }
        return token;
    }
}
