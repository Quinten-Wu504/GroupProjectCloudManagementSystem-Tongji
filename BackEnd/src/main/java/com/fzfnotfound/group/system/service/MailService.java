package com.fzfnotfound.group.system.service;


import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Email;
import com.fzfnotfound.group.system.entity.Sms;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface MailService {

    public String sendTemplateMail(String receiveEmail) throws MessagingException, UnsupportedEncodingException;
    public ApiResponse<String> checkCode(Email emailEntity);
}
