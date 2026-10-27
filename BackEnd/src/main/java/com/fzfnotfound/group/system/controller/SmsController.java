package com.fzfnotfound.group.system.controller;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.service.PhoneNumberService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import jakarta.annotation.Resource;
import org.apache.http.HttpException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/*
 *
 *@brief: 验证码控制
 *
 *
 *
 * */

@CrossOrigin
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private PhoneNumberService phoneNumberService;

    @PostMapping("/getCode")
    public String getCode(@RequestParam("phoneNumber") String phoneNumber,@RequestParam("choice")int choice){
        if(phoneNumber==null){
            return ("手机号为空");
        }
        return phoneNumberService.sendVerification(phoneNumber,choice);
    }
    @PostMapping("/check")
    public ApiResponse<String> checkCode(Sms smsCode) {
        if(smsCode==null){
            return ApiResponse.error("传入参数为空");
        }
        System.out.println("first"+smsCode.getPhoneNumber());
        String code = smsCode.getCode();
        if(code==null){
            return ApiResponse.error("手机号为空");
        }
        System.out.println("first"+smsCode.getPhoneNumber());
        return phoneNumberService.checkCode(smsCode);
    }


}
