package com.hjf.controller;

import com.hjf.entity.LoginInfo;
import com.hjf.entity.Result;
import com.hjf.entity.Syster;
import com.hjf.service.SysterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private SysterService systerService;

    @PostMapping("/login")
    public Result login(@RequestBody Syster syster){
        LoginInfo loginInfo =systerService.login(syster);
        if (loginInfo != null){
            return Result.success(loginInfo);
        }
        return Result.fail("用户名或密码错误");
    }
}
