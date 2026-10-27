package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;

public interface UserLogin {
        //账号+密码
        public ApiResponse<String> loginByAccount(User user);
        //手机号+验证码
        public ApiResponse<String> loginByPhoneAndVerification(User user);
        //手机号+密码
        public ApiResponse<String> loginByPhoneAndPassword(User user);


}
