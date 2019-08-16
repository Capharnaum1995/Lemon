package com.lemon.community.provider;

import com.alibaba.fastjson.JSON;
import com.lemon.community.dto.AccessTokenDTO;
import com.lemon.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 这个类是用来提供实现Github第三方登陆的，所以隔离到一个单独的包（编程的习惯）。@Component注解一个类之后，相当于已经自动地将该类的对象实例化了，并且放在了一个pool中，我们就不需要再new对象（Spring的强大之处）。
 */
@Component
public class GithubProvider {
    //定义获取token的方法，得到了token就可以调用用户信息的API,需要引入okhttp的maven
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()////1.创建request对象
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            //System.out.println(string);
            //得到的string格式为：access_token=d41211bda92a0ebf0904aabbf1ee71dee9723138&scope=user&token_type=bearer
            //字符拆分，得到token
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //定义得到用户的方法:通过得到的access_token，创建一个request请求,执行请求得到response
    public GithubUser getUser(String access_token) {
        OkHttpClient client = new OkHttpClient();
        //1.创建request对象
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + access_token)
                .build();
        //执行request请求，得到response.response实际上是用户信息的API
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            return JSON.parseObject(string, GithubUser.class);
        } catch (IOException e) {
        }
        return null;
    }
}
