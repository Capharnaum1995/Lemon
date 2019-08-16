package com.lemon.community.dto;

import lombok.Data;

//我们只需要得到的用户信息
@Data
public class GithubUser {
    private String name;
    private Long id;//防止Github的用户暴增，设置为Long类型的id
    private String bio;//用户的信息描述
    private String avatarUrl;
}
