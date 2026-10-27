package com.fzfnotfound.group.system.controller;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.entity.UserManageControllerEntity;
import com.fzfnotfound.group.system.service.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@CrossOrigin
@RestController
public class UserManageController {
    @Autowired
    private UserManagement userManagement;


    @PostMapping("/changePassword")
    public ApiResponse<String> changePassword(@RequestBody User user){
        return userManagement.passwordChange(user);
    }

    @PostMapping("/changeName")
    public ApiResponse<String> changeName(@RequestBody User user){
        return userManagement.nameChange(user);
    }

    @PostMapping("/uploadUserHeadPortrait")
    public ApiResponse<String> uploadHeadPortrait(@RequestParam("account")String account,@RequestParam("image")MultipartFile image){
        return userManagement.updateUserHeadPortrait(account,image);
    }

    @GetMapping("/deleteAccount")
    public ApiResponse<String> deleteAccount(@RequestParam("account")String account){
        return userManagement.deleteAccount(account);
    }

}

