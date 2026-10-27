package com.fzfnotfound.group.system.controller;


import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.service.InfoGet;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class InfoGetController {

    @Autowired
    private InfoGet infoGet;

    //获取个人信息(需要account)
    @PostMapping("/getUserInfo")
    public ApiResponse<User> getUserInfo(@RequestBody User user) throws IOException {
        return infoGet.getUserInfo(user);
    }
    //获取个人加入的小组信息()
    @PostMapping("/getGroupInfo")
    public ApiResponse<List<GroupDescription>> getGroupInfo(@RequestBody User user) throws IOException {
        return infoGet.getGroupInfo(user);
    }
    //获取小组的一级备选任务
    @PostMapping("/getAlternativeTask")
    public ApiResponse<List<Task>> getAlternativeTask(@RequestBody Group group){
        return infoGet.getAlternativeTask(group);
    }
    //获取小组的一级活跃任务
    @PostMapping("/getActiveTask")
    public ApiResponse<List<Task>> getActiveTask(@RequestBody Group group){
        return infoGet.getActiveTask(group);
    }

    //获取小组的所有活跃的父任务
    @PostMapping("/getActiveParentTask")
    public ApiResponse<List<Task>> getActiveParentTask(@RequestBody Group group){
        return infoGet.getActiveParentTask(group);
    }

    //获取一级任务的所有子任务
    @PostMapping("/getTaskDistribute")
    public ApiResponse<List<Task>> getTaskDistribute(@RequestBody Task task){
        return infoGet.getTaskDistribute(task);
    }

    @GetMapping("/getAllUsers")
    ApiResponse<List<User>> getAllUsers(@RequestParam("groupId")int groupId) throws IOException{
        return infoGet.getAllUsers(groupId);
    }

    @PostMapping("/getGroupApplication")
    public ApiResponse<List<GroupApplication>> getGroupApplication(@RequestBody User user){
        return infoGet.getGroupApplication(user);
    }


    @PostMapping("/getUserInvitedInfo")
    public ApiResponse<List<GroupApplication>> getUserInvitedInfo(@RequestBody User user){
        return infoGet.getUserInvitedInfo(user);
    }

    @GetMapping("/getAllGroups")
    public ApiResponse<List<GroupDescription>> getAllGroups(){
        return infoGet.getAllGroups();
    }

    @GetMapping("/getAllGroupMembers")
    public ApiResponse<List<GroupMemberDescription>> getAllGroupMembers(@RequestParam("groupId")int groupId) throws IOException {
        return infoGet.getAllGroupMembers(groupId);
    };

    @GetMapping("/getLibraryInfo")
    public ApiResponse<List<ResourceDescription>> getLibraryInfo(@RequestParam("groupId")int groupId){
        return infoGet.getLibraryInfo(groupId);
    }

    @PostMapping("/getMessageInfo")
    public ApiResponse<List<MessageDescription>> getMessageInfo(@RequestBody GetMessageInfo getMessageInfo) throws IOException{
        if(getMessageInfo.getChoice()==null) {
            if(getMessageInfo.getId()==null){
                System.out.println("choice和id为null");
                return ApiResponse.error("choice和id为null");
            }
            System.out.println("仅choice为null");
            return ApiResponse.error("仅choice为null");
        }
        if(getMessageInfo.getId()==null){
            System.out.println("仅id为null");
            return ApiResponse.error("仅id为null");
        }
        return infoGet.getMessageInfo(getMessageInfo.getChoice(),getMessageInfo.getId());
    }

    @GetMapping("/getCurrentTaskList")
    public ApiResponse<List<TaskDescription>> getCurrentTaskList(@RequestParam("groupId")int groupId){
        return infoGet.getCurrentTaskList(groupId);
    }

    @PostMapping("/getReplyInfoByMessageId")
    public ApiResponse<ReplyDescription> getReplyInfo(@RequestBody Message message) throws IOException{
        return infoGet.getReplyInfoByMessageId(message.getMessageId());
    }

    @PostMapping("/getResourceInfoByTaskId")
    public ApiResponse<List<Resource>> getResourceInfoBySonTaskId(@RequestBody Task task){
        return infoGet.getResourceInfoByTaskId(task.getTaskId());
    }
    @GetMapping("/getGroupDescriptionById")
    public ApiResponse<GroupDescription> getGroupDescriptionById(@RequestParam("groupId")int groupId) throws IOException {
        return infoGet.getGroupDescriptionById(groupId);
    }

    @PostMapping("/getResourceByMessageId")
    public ApiResponse<Resource> getResourceByMessageId(@RequestBody Message message){
        return infoGet.getResourceByMessageId(message.getMessageId());
    }

    @PostMapping("/getDutyByAccount")
    public ApiResponse<List<Task>> getDutyByAccount(@RequestBody User user){
        if(user.getAccount()==null)
            return ApiResponse.error("错误，account为空");
        return infoGet.getDutyByAccount(user.getAccount());
    }
}
