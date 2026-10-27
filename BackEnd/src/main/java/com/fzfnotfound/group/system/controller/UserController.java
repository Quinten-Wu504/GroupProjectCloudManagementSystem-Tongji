package com.fzfnotfound.group.system.controller;


import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.service.*;
import com.fzfnotfound.group.system.service.serviceImpl.UserManagementImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserLogin userLogin;

    @Autowired
    private UserRegister userRegister;
    @Autowired
    private UserManagement userManagement;

    private MailService mailService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody User user){
        System.out.println(user.getAccount());
        return userRegister.register(user);

    }


    @PostMapping("/find")
    public String select(int id){

        return "123";
    }
    //登录，有可能是账号或者手机号
    @PostMapping("/login")
    public ApiResponse<String> login( @RequestBody User user){

        String account=user.getAccount();
        //账号密码
        if(account!=null&&account.indexOf("GPC")!=-1){
            return userLogin.loginByAccount(user);
        }
        //手机号密码
        else{
            return userLogin.loginByPhoneAndPassword(user);
        }

    }
    //手机号+验证码
    @PostMapping("/loginV")
    public ApiResponse<String> loginV(@RequestBody User user){
        return userLogin.loginByPhoneAndVerification(user);
    }


}
