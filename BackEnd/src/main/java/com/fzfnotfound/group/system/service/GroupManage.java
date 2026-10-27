package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Group;
import com.fzfnotfound.group.system.entity.GroupApplication;
import com.fzfnotfound.group.system.entity.GroupMember;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupManage {
    public ApiResponse<String> createGroup(Group group);

    public ApiResponse<String> applyToGroup(GroupApplication groupApplication);

    public ApiResponse<String> quitGroup(GroupMember groupMember);

    public ApiResponse<String> transferLeader(String presentLeaderAccount, String nextLeaderAccount,Integer groupId);

    public ApiResponse<String> updateGroupHeadPortrait(int groupId, MultipartFile file);

    public ApiResponse<String> deleteGroup(String account,int groupId);

    public ApiResponse<String> removeMember(String account,int groupId);

    public ApiResponse<List<Group>> searchGroup(String textInput);
    //？
    public ApiResponse<String> updateApplicationStatus(String account,int groupId,int status,String reviewer);
    //
    public ApiResponse<String> changeGroup(Group group);

    public ApiResponse<Integer> changeGroupStatus(Group group);
}
