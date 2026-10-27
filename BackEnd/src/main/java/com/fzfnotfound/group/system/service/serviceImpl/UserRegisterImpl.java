package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.mapper.UserMapper;
import com.fzfnotfound.group.system.service.PhoneNumberService;
import com.fzfnotfound.group.system.service.UserRegister;
import com.fzfnotfound.group.system.utility.RandomManage;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;



@Service
public class UserRegisterImpl implements UserRegister {

    @Autowired
    private UserMapper usermapper;

    @Autowired
    PhoneNumberService phoneNumberService;


    @Override
    public ApiResponse<String> register(User user) {
        //获取昵称
        String nickname=user.getNickname();
        String pwd=user.getPassword();
        String phoneNumber=user.getPhoneNumber();
        String code=user.getCode();
//        //1.用户名合法性检查
//        if(!account.matches("^[a-zA-Z0-9_]+$"))
//            return "用户名不合法";
//        //用户名是否唯一
//        List<User> userList=usermapper.selectByAccount(user.getAccount());
//        if(!userList.isEmpty())
//            return "用户名不唯一";

        //2.密码合法性检查
        //不少于8位
        if(pwd.length()<8)
            return ApiResponse.error("密码不得少于8位");
        //不包含空格
        if(pwd.contains(" "))
            return ApiResponse.error("密码不得包含空格");
        //字符类型不少于3种
        int count=0;
        if(pwd.matches(".*[a-z].*"))
            count++;
        if(pwd.matches(".*[A-Z].*"))
            count++;
        if(pwd.matches(".*[0-9].*"))
            count++;
        if(pwd.contains("!")||pwd.contains("@")||
           pwd.contains("$")||pwd.contains("%")||
           pwd.contains("#")||pwd.contains("^")||
           pwd.contains("&")||pwd.contains("*")||
           pwd.contains("(")||pwd.contains(")")||
           pwd.contains("_")||pwd.contains("+")||
           pwd.contains("-")||pwd.contains("=")||
           pwd.contains("{")||pwd.contains("}")||
           pwd.contains("[")||pwd.contains("]")||
           pwd.contains(":")||pwd.contains(";")||
           pwd.contains("\"")||pwd.contains("'")||
           pwd.contains("<")||pwd.contains(">")||
           pwd.contains(",")||pwd.contains(".")||
           pwd.contains("?")||pwd.contains("/")||
           pwd.contains("|")||pwd.contains("\\")||
           pwd.contains("~")||pwd.contains("`"))
            count++;
        if(count<3)
            return ApiResponse.error("字符类型不得少于三种");

// 测试阶段暂时注释，这里有判断手机号重复的逻辑?
        //验证码正确
        Sms sms=new Sms();
        sms.setCode(user.getCode());
        sms.setPhoneNumber(user.getPhoneNumber());
        //验证码部分检查
        if(!phoneNumberService.checkCode(sms).isSuccess()){
            return ApiResponse.error("验证码不正确");
        }
        String account="GPC";
        while (true){
            //分配账号
            Random random= new Random(10);
            account+= RandomManage.randomNumber(8);
            if(usermapper.selectByAccount(account).isEmpty()){
                user.setAccount(account);
                break;
            }
        }
        //添加注册时间
        user.setDateFirstRegister(LocalDateTime.now());
        //添加登录时间
        user.setLastLogin(LocalDateTime.now());
        usermapper.addUser(user);
        return ApiResponse.success(account);
    }

    @Override
    public String certification(User user)
    {
        String email=user.getEmail();

        //检查验证码

        //用户信息添加到数据库
        usermapper.addUser(user);
        return "success";
    }
}
