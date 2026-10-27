package com.fzfnotfound.group.system.controller;


import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.DeadlineInfo;
import com.fzfnotfound.group.system.entity.SendInformation;
import com.fzfnotfound.group.system.service.SendInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class InformationManagement {

    @Autowired
    SendInfo sendInfo;


    @PostMapping("/sendDeadlineInfo")
    ApiResponse<List<DeadlineInfo>> sendDeadlineInfo(@RequestBody SendInformation sendInformation){
        String account=sendInformation.getAccount();
        if (account==null){
            return ApiResponse.error("未传入账号");
        }
        return sendInfo.sendDeadlineInfo(account);
    }

}
