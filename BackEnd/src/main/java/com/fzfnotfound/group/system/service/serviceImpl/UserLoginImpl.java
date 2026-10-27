package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.mapper.UserMapper;
import com.fzfnotfound.group.system.service.PhoneNumberService;
import com.fzfnotfound.group.system.service.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserLoginImpl implements UserLogin {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PhoneNumberService phoneNumberService;
    @Override
    public ApiResponse<String> loginByAccount(User user) {
        //如果未找到用户名
        List<User> users=userMapper.selectByAccount(user.getAccount());
        if(users.isEmpty()){
            return ApiResponse.error("未找到用户名");
        }
        //如果用户名和密码有一致的情况
        for(User res:users){
            if(res!=null&&res.getPassword().equals(user.getPassword())){
                //更新登录时间
                userMapper.updateLoginTime(res.getAccount(), LocalDateTime.now());
                String userAccount = res.getAccount();
                return ApiResponse.success(userAccount);
            }
        }
        //如果用户名和密码不一致
        return ApiResponse.error("用户名和密码不匹配");
    }

    @Override
    public ApiResponse<String> loginByPhoneAndVerification(User user) {
        //如果未找到手机号
        User user1=userMapper.selectUserByPhoneNumber(user.getPhoneNumber());
        if(user1==null){
            return ApiResponse.error("未找到手机号");
        }
        Sms sms=new Sms();
        sms.setCode(user.getCode());
        sms.setPhoneNumber(user.getPhoneNumber());
        //验证码部分检查
        if(!phoneNumberService.checkCode(sms).isSuccess()){
            return ApiResponse.error("验证码不正确");
        }

        return ApiResponse.success(user1.getAccount());
    }

    @Override
    public ApiResponse<String> loginByPhoneAndPassword(User user) {
        //如果未找到手机号
        List<User> users=userMapper.selectByPhoneNumber(user.getPhoneNumber());
        if(users.isEmpty()){
            return ApiResponse.error("未找到手机号");
        }
        //如果手机号和密码有一致的情况
        for(User res:users){
            if(res!=null&&res.getPassword().equals(user.getPassword())){
                String userAccount = res.getAccount();
                return ApiResponse.success(userAccount);
            }
        }
        //手机号和密码不一致
        return ApiResponse.error("手机号和密码不匹配");
    }
}
