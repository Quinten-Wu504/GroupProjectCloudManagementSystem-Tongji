package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.DeadlineInfo;
import com.fzfnotfound.group.system.entity.SendInformation;

import java.util.List;

public interface SendInfo {

    public ApiResponse<List<DeadlineInfo>> sendDeadlineInfo(String account);
}
