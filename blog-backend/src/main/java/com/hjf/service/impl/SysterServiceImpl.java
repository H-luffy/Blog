package com.hjf.service.impl;

import com.hjf.entity.LoginInfo;
import com.hjf.entity.Syster;
import com.hjf.mapper.SysterMapper;
import com.hjf.service.SysterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysterServiceImpl implements SysterService {

    @Autowired
    private SysterMapper systerMapper;


    @Override
    public LoginInfo login(Syster syster) {
        Syster systerLogin =systerMapper.getUsernameAndPassword(syster);
        if (systerLogin != null){
            LoginInfo loginInfo = new LoginInfo(systerLogin.getId(),systerLogin.getUsername(),null);
            return loginInfo;
        }
        return null;
    }
}
