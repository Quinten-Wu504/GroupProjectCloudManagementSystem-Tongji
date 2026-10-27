package com.fzfnotfound.group.system.controller;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Task;
import com.fzfnotfound.group.system.service.GroupTaskManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class GroupTaskManageController {

    @Autowired
    private GroupTaskManage groupTaskManage;

    //新建任务
    @PostMapping("/createTask")
    public ApiResponse<String> createTask(@RequestBody Task task){
        return groupTaskManage.createTask(task);
    }
    //任务添加(即状态设置为启用)并添加起止时间，提醒时间和主负责人
    @PostMapping("/addTask")
    public ApiResponse<String> addTask(@RequestBody Task task){
        return groupTaskManage.addTask(task);
    }
    //任务删除
    @PostMapping("/deleteTask")
    public ApiResponse<String> deleteTask(@RequestBody Task task){
        return groupTaskManage.deleteTask(task);
    }
    //任务从启用列表移除
    @PostMapping("/taskSetNotActive")
    public ApiResponse<String> taskSetNotActive(@RequestBody Task task){
        return groupTaskManage.taskSetNotActive(task);
    }
    //修改备用任务
    @PostMapping("/changeAlternativeTask")
    public ApiResponse<String> changeAlternativeTask(@RequestBody Task task){
        return groupTaskManage.changeAlternativeTask(task);
    }
    //修改启用任务
    @PostMapping("changeActiveTask")
    public ApiResponse<String> changeActiveTask(@RequestBody Task task){
        return groupTaskManage.changeActiveTask(task);
    }
}
