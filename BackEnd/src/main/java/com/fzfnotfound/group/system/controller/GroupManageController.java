package com.fzfnotfound.group.system.controller;

import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.service.GroupManage;
import com.fzfnotfound.group.system.service.InfoGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class GroupManageController {
    @Autowired
    private GroupManage groupManage;

    //创建小组
    //返回Group类型，若为null创建失败
    @PostMapping("/createGroup")
    public ApiResponse<String> createGroup(@RequestBody Group group){
        return groupManage.createGroup(group);
    }

    //申请加入小组(加入审批列表)
    //包括自己申请和别人邀请，applicant_account有所不同
    //返回String类型，success成功,failure失败
    @PostMapping("/applyToGroup")
    public ApiResponse<String> applyToGroup(@RequestBody GroupApplication groupApplication){
        return groupManage.applyToGroup(groupApplication);
    }

    //退出小组(从GroupMember表中移除)
    @PostMapping("/quitGroup")
    public ApiResponse<String> quitGroup(@RequestBody GroupMember groupMember){
        return groupManage.quitGroup(groupMember);
    }
    //转让组长
    @PostMapping("/transferLeader")
    public ApiResponse<String> transferLeader(@RequestBody TransferLeader transferLeader){
        return groupManage.transferLeader(transferLeader.getPresentLeaderAccount(),transferLeader.getNextLeaderAccount(),transferLeader.getGroupId());
    }
    //上传小组头像
    @PostMapping("/uploadGroupHeadPortrait")
    public ApiResponse<String> uploadHeadPortrait(@RequestBody uploadHeadPortrait uploadHeadPortrait){
        return groupManage.updateGroupHeadPortrait(uploadHeadPortrait.getGroupId(),uploadHeadPortrait.getImage());
    }
    //解散小组
    @PostMapping("/deleteGroup")
    public ApiResponse<String> deleteGroup(@RequestBody GroupMember groupMember){
        return groupManage.deleteGroup(groupMember.getAccount(),groupMember.getGroupId());
    }

    //移除组员
    @PostMapping("/removeMember")
    public ApiResponse<String> removeMember(@RequestBody GroupMember groupMember){
        return groupManage.removeMember(groupMember.getAccount(),groupMember.getGroupId());
    }

    @PostMapping("/searchGroup")
    public ApiResponse<List<Group>> searchGroup(@RequestBody TextInput textInput){
        return groupManage.searchGroup(textInput.getTextInput());
    }

    //更改group_application状态，即用户的接受/拒绝邀请 或者 组长的接受/拒绝加入请求
    @PostMapping("/updateApplicationStatus")
    public ApiResponse<String> updateApplicationStatus(@RequestBody GroupApplication groupApplication){
        return groupManage.updateApplicationStatus(groupApplication.getAccount(),groupApplication.getGroupId(),groupApplication.getStatus(),groupApplication.getReviewer());
    }
    @PostMapping("/changeGroup")
    public ApiResponse<String> changeGroup(@RequestBody Group group){
        return groupManage.changeGroup(group);
    }

    //更改小组状态
    @PostMapping("changeGroupState")
    public ApiResponse<Integer> changeGroupState(@RequestBody Group group){
        return groupManage.changeGroupStatus(group);
    }
}
