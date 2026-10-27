package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface InfoGet {
    public ApiResponse<User> getUserInfo(User user) throws IOException;

    public ApiResponse<List<GroupDescription>> getGroupInfo(User user) throws IOException;

    public ApiResponse<List<Task>> getAlternativeTask(Group group);

    public ApiResponse<List<Task>> getActiveParentTask(Group group);

    public ApiResponse<List<Task>> getActiveTask(Group group);

    public ApiResponse<List<Task>> getTaskDistribute(Task task);

    public ApiResponse<List<MessageDescription>> getMessageInfo(int choice, int id) throws IOException;

    public ApiResponse<List<GroupMember>> getAllMembers(int groupId);

    public ApiResponse<List<User>> getAllUsers (int groupId) throws IOException;

    public ApiResponse<List<GroupApplication>> getGroupApplication(User user);


    public ApiResponse<List<GroupApplication>> getUserInvitedInfo(User user);

    public ApiResponse<List<GroupDescription>> getAllGroups();

    public ApiResponse<List<GroupMemberDescription>> getAllGroupMembers(int groupId) throws IOException;

    public ApiResponse<List<ResourceDescription>> getLibraryInfo(int groupId);

    public ApiResponse<List<TaskDescription>> getCurrentTaskList(int groupId);

    public ApiResponse<ReplyDescription> getReplyInfoByMessageId(int messageId) throws IOException;

    public ApiResponse<List<Resource>> getResourceInfoByTaskId(int taskId);

    public ApiResponse<GroupDescription> getGroupDescriptionById(int groupId) throws IOException;

    public ApiResponse<Resource> getResourceByMessageId(int messageId);

    public ApiResponse<List<Task>> getDutyByAccount(String account);
}
