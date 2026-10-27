package com.fzfnotfound.group.system.controller;


import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Email;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.service.MailService;
import com.fzfnotfound.group.system.service.PhoneNumberService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin
@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/getCode")
    public String getCode(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {

        if(email==null){
            return ("邮箱为空");
        }

        return mailService.sendTemplateMail(email);
    }
    @PostMapping("/check")
    public ApiResponse<String> checkCode(@RequestBody Email emailCode) {
        if(emailCode==null){
            return ApiResponse.error("发送邮箱为空");
        }
        System.out.println("first"+emailCode.getEmail());
        String code = emailCode.getCode();
        if(code==null){
            return ApiResponse.error("验证码为空");
        }
        return mailService.checkCode(emailCode);
    }
}
