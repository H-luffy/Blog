package com.hjf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 登录信息
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private Long id;
    private String username;
    private String token;
}
