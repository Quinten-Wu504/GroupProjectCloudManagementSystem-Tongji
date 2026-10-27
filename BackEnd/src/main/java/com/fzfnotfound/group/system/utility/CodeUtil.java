package com.fzfnotfound.group.system.utility;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CodeUtil {

    //获取手机号缓存里的验证码
    @Cacheable(key ="#phoneNumber" ,value = "smsCode")
    public String getCacheCode(String phoneNumber){
        return null;
    }

    public String generate(String phoneNumber){
        String code="";
        //产生四位验证码
        code+= RandomManage.randomNumber(4);

        System.out.println(code);
        return code;
    }

    //获取邮箱缓存里的验证码
    @Cacheable(key ="#email" ,value = "emailCode")
    public String getEmailCacheCode(String email){
        return null;
    }



}
