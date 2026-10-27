package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserManagement {
    //更改用户名
    ApiResponse<String> nameChange(User user);
    //更改密码
    ApiResponse<String> passwordChange(User user);
    //注销用户
    ApiResponse<String> deleteAccount(String account);
    //更新用户头像
    ApiResponse<String> updateUserHeadPortrait(String account,MultipartFile file);
}
