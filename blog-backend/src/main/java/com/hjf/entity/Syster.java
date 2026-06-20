package com.hjf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
* 系统用户
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Syster {
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private LocalDateTime createTime;
}
