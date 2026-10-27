package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Task;

public interface GroupTaskManage {
    public ApiResponse<String> createTask(Task task);

    public ApiResponse<String> addTask(Task task);

    public ApiResponse<String> deleteTask(Task task);

    public ApiResponse<String> taskSetNotActive(Task task);

    public ApiResponse<String> changeAlternativeTask(Task task);

    public ApiResponse<String> changeActiveTask(Task task);

    public ApiResponse<String> deleteTaskWhenDeleteGroup(Task task);
}
