package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Sms;
import com.fzfnotfound.group.system.entity.User;

public interface PhoneNumberService {

    public String sendVerification(String mobile,int choice);
    public ApiResponse<String> checkCode(Sms sms);
}
