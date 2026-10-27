package com.fzfnotfound.group.system.service.serviceImpl;

import com.aliyuncs.exceptions.ClientException;
import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.mapper.UserMapper;
import com.fzfnotfound.group.system.service.PhoneNumberService;
import com.fzfnotfound.group.system.utility.AliyunSmsUtil;
import com.fzfnotfound.group.system.utility.CodeUtil;
import com.fzfnotfound.group.system.utility.RandomManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;


@Service
public class PhoneNumberServiceImpl implements PhoneNumberService {

    @Autowired
    CodeUtil codeUtil;
    @Autowired
    private UserMapper userMapper;

    @Override
    @CachePut(key = "#phoneNumber",value = "smsCode")
    public String sendVerification(String phoneNumber,int choice) {
        //电话号码合法
//        if ((phoneNumber != null) && (!phoneNumber.isEmpty())) {
  //          if (!Pattern.matches("^1[3-9]\\d{9}$", phoneNumber)){
   //             return "fail";
   //         }
   //     }
        //判断手机号是否重复(注册时判断)
        if(choice==0)
            if(userMapper.selectUserByPhoneNumber(phoneNumber)!=null)
                return "repeated";

        String code= codeUtil.generate(phoneNumber);
        System.out.println(code);
        try{
            AliyunSmsUtil.sendSmsByParams(phoneNumber,code);
            return (code);
        }catch (ClientException e){
            e.printStackTrace();
        }
        return code;
    }

    @Override
    public ApiResponse<String> checkCode(Sms sms) {
        String code=sms.getCode();
        System.out.println("PhoneNumber:"+sms.getPhoneNumber());
        String cacheCode=codeUtil.getCacheCode(sms.getPhoneNumber());
        System.out.println("cacheCode:"+cacheCode);
        return code.equals(cacheCode)?ApiResponse.success("验证成功"):ApiResponse.error("验证码错误");
    }
}
