package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;

public interface UserRegister {
    //用户名和密码
    ApiResponse<String> register(User user);
    //邮箱验证
    String certification(User user);
}
