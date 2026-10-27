package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.entity.Record;
import com.fzfnotfound.group.system.mapper.GroupMapper;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.service.InfoGet;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
@Service
public class InfoGetImpl implements InfoGet {

    @Autowired
    private InfoGetMapper infoGetMapper;

    @Override
    public ApiResponse<User> getUserInfo(User user) throws IOException {
        User user1 = infoGetMapper.selectByAccount(user.getAccount());
        String imgPath = "images/users" + user.getHeadPortrait();
        imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
        //传入头像
        final byte[] bytes = IOUtils.toByteArray(
                new FileInputStream(new File(imgPath)));

        String headPortrait = Base64.getEncoder().encodeToString(bytes);
        user1.setHeadPortrait(headPortrait);
        return ApiResponse.success(user1);
    }

    @Override
    public ApiResponse<List<GroupDescription>> getGroupInfo(User user) throws IOException {

        List<GroupMember> groupIdList = infoGetMapper.selectEnteredGroupId(user);
        //未加入小组
        if (groupIdList.isEmpty())
            return ApiResponse.success(null);

        List<GroupDescription> result = new ArrayList<>();
        for (int m = 0; m < groupIdList.size(); m++) {
            Group group = infoGetMapper.selectGroup(groupIdList.get(m).getGroupId());
            GroupDescription groupDescription = new GroupDescription();
            groupDescription.setGroupId(group.getGroupId());
            groupDescription.setGroupName(group.getGroupName());
            groupDescription.setState(group.getState());
            groupDescription.setDescription(group.getDescription());
            //拿图片
            String imgPath = "";
            imgPath = Paths.get("images/groups", group.getHeadPortrait()).toAbsolutePath().toString();
            //传入头像
            final byte[] bytes = IOUtils.toByteArray(
                    new FileInputStream(new File(imgPath)));

            String headPortrait = Base64.getEncoder().encodeToString(bytes);
            groupDescription.setHeadPortrait(headPortrait);
            groupDescription.setCreateTime(group.getCreateTime());
            User user1 = new User();
            user1.setAccount(group.getLeaderAccount());
            //组长账户
            groupDescription.setLeaderAccount(infoGetMapper.selectByAccount(user1.getAccount()).getAccount());
            //组长昵称
            groupDescription.setLeaderNickname(infoGetMapper.selectByAccount(user1.getAccount()).getNickname());
            //小组人数
            groupDescription.setHeadcount(infoGetMapper.getMemberCount(group.getGroupId()));
            result.add(groupDescription);
        }
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<List<Task>> getAlternativeTask(Group group) {
        return ApiResponse.success(infoGetMapper.selectAlternativeTask(group.getGroupId()));
    }


    @Override
    public ApiResponse<List<Task>> getActiveParentTask(Group group) {
        return ApiResponse.success(infoGetMapper.selectTopActiveTask(group.getGroupId()));
    }

    @Override
    public ApiResponse<List<Task>> getActiveTask(Group group) {
        return ApiResponse.success(infoGetMapper.selectActiveTask(group.getGroupId()));
    }

    @Override
    public ApiResponse<List<Task>> getTaskDistribute(Task task) {
        //返回的结果
        List<Task> result = new ArrayList<>();
        //用于循环查找
        List<Task> pre = infoGetMapper.selectSonTask(task.getTaskId());
        //没有子任务
        if (pre.isEmpty())
            return null;
        result.addAll(pre);
        List<Task> next = new ArrayList<>();
        boolean start = true;
        while (!next.isEmpty() || start) {
            start = false;
            next.clear();
            for (int m = 0; m < pre.size(); m++) {
                List<Task> temp = infoGetMapper.selectSonTask(pre.get(m).getTaskId());
                if (!temp.isEmpty())
                    next.addAll(temp);
            }
            result.addAll(next);
            pre.clear();
            pre.addAll(next);
        }
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<List<MessageDescription>> getMessageInfo(int choice, int id) throws IOException {
        //特定留言，id为messageId
        if (choice == 0) {
            List<Message> result = new ArrayList<>();
            result.add(infoGetMapper.getMessageByMessageId(id));
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.add(temp);
            }
            return ApiResponse.success(realresult);
        }
        else if (choice == 1) {//关联某任务的留言，id为taskId
            List<Message> result = infoGetMapper.getMessageByTaskId(id, 0);
            result.addAll(infoGetMapper.getMessageByTaskId(id, 1));
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.sort(Comparator
                        .comparingInt((MessageDescription app) -> app.getMessageType() == 1 ? 0 : 1)
                        .thenComparing(MessageDescription::getTime,Comparator.reverseOrder())
                );
                realresult.add(temp);
            }
            return ApiResponse.success(realresult);
        }
        else if (choice == 2) {//关联某资源的留言，id为resourceId
            List<Message> result = infoGetMapper.getMessageByResourceId(id, 0);
            result.addAll(infoGetMapper.getMessageByResourceId(id, 1));
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.sort(Comparator
                        .comparingInt((MessageDescription app) -> app.getMessageType() == 1 ? 0 : 1)
                        .thenComparing(MessageDescription::getTime,Comparator.reverseOrder())
                );
                realresult.add(temp);
            }
            return ApiResponse.success(realresult);
        }
        else if (choice == 3) {//小组的精华留言，id为groupId
            List<Task> groupTask = infoGetMapper.selectActiveTask(id);
            List<Resource> groupResource = infoGetMapper.getResourceByGroupId(id);
            List<Message> result = new ArrayList<>();
            //关联任务的精华留言
            for (int m = 0; m < groupTask.size(); m++) {
                result.addAll(infoGetMapper.getMessageByTaskId(groupTask.get(m).getTaskId(), 1));
            }
            //仅关联资源的精华留言
            for (int m = 0; m < groupResource.size(); m++) {
                result.addAll(infoGetMapper.getResourceOnlyMessageByResourceId(groupResource.get(m).getResourceId(), 1));
            }
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.add(temp);
            }
            realresult.sort(Comparator
                    .comparingInt((MessageDescription app) -> app.getMessageType() == 1 ? 0 : 1)
                    .thenComparing(MessageDescription::getTime,Comparator.reverseOrder())
            );
            return ApiResponse.success(realresult);
        }
        else if (choice == 4) {//小组所有关联资源的留言，id为groupId
            List<Resource> groupResource = infoGetMapper.getResourceByGroupId(id);
            List<Message> result = new ArrayList<>();
            //关联资源的留言
            for (int m = 0; m < groupResource.size(); m++) {
                result.addAll(infoGetMapper.getMessageByResourceId(groupResource.get(m).getResourceId(), 0));
                result.addAll(infoGetMapper.getMessageByResourceId(groupResource.get(m).getResourceId(), 1));
            }
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.add(temp);
            }
            realresult.sort(Comparator
                    .comparingInt((MessageDescription app) -> app.getMessageType() == 1 ? 0 : 1)
                    .thenComparing(MessageDescription::getTime,Comparator.reverseOrder())
            );
            return ApiResponse.success(realresult);
        }
        else if (choice == 5) {//获取小组全部留言，id为groupId
            List<Task> groupTask = infoGetMapper.selectTaskByGroupId(id);
            List<Resource> groupResource = infoGetMapper.getResourceByGroupId(id);
            List<Message> result = new ArrayList<>();
            //关联任务的留言
            for (int m = 0; m < groupTask.size(); m++) {
                result.addAll(infoGetMapper.getMessageByTaskId(groupTask.get(m).getTaskId(), 0));
                result.addAll(infoGetMapper.getMessageByTaskId(groupTask.get(m).getTaskId(), 1));
            }
            //仅关联资源的留言
            for (int m = 0; m < groupResource.size(); m++) {
                result.addAll(infoGetMapper.getResourceOnlyMessageByResourceId(groupResource.get(m).getResourceId(), 0));
                result.addAll(infoGetMapper.getResourceOnlyMessageByResourceId(groupResource.get(m).getResourceId(), 1));
            }
            List<MessageDescription> realresult = new ArrayList<>();
            for (int m = 0; m < result.size(); m++) {
                MessageDescription temp = new MessageDescription();
                temp.setMessageId(result.get(m).getMessageId());
                temp.setTitle(result.get(m).getTitle());
                temp.setContent(result.get(m).getMessageContent());
                temp.setTaskId(result.get(m).getTaskId());
                temp.setMessageType(result.get(m).getMessageType());
                temp.setTime(result.get(m).getPostedTime());

                //添加头像
                User user1 = infoGetMapper.selectByAccount(result.get(m).getPostAccount());
                String imgPath = "images/users" + result.get(m).getHeadPortrait();
                imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
                //传入头像
                final byte[] bytes = IOUtils.toByteArray(
                        new FileInputStream(new File(imgPath)));
                String headPortrait = Base64.getEncoder().encodeToString(bytes);
                temp.setHeadPortrait(headPortrait);
                //添加昵称
                temp.setNickname(user1.getNickname());
                //添加taskName
                //当前任务
                Task currentTask = infoGetMapper.selectTaskById(temp.getTaskId());
                //有父任务
                if (currentTask.getParentId() != null) {
                    String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(parentName + ": " + currentName);
                } else//无父任务
                {
                    String currentName = currentTask.getTaskName();
                    temp.setTaskName(currentName);
                }
                //添加answerNum
                temp.setAnswerNum(infoGetMapper.getReplyCount(temp.getMessageId()));
                realresult.add(temp);
            }
            realresult.sort(Comparator
                    .comparingInt((MessageDescription app) -> app.getMessageType() == 1 ? 0 : 1)
                    .thenComparing(MessageDescription::getTime,Comparator.reverseOrder())
            );
            return ApiResponse.success(realresult);
        }
        else {//choice不符合预期
            return ApiResponse.error("choice不符合预期");
        }
    }


    @Override
    public ApiResponse<List<GroupMember>> getAllMembers(int groupId) {


        return null;
    }

    @Override
    public ApiResponse<List<User>> getAllUsers(int groupId) throws IOException{
        List<User>result=infoGetMapper.selectAllUsers(groupId);
        //填入头像
        for(var groupMemberDesc:result){
            User user = infoGetMapper.selectByAccount(groupMemberDesc.getAccount());
            String imgPath;
            imgPath = Paths.get("images/users", user.getHeadPortrait()).toAbsolutePath().toString();
            //传入头像
            final byte[] bytes = IOUtils.toByteArray(
                    new FileInputStream(imgPath));

            String headPortrait = Base64.getEncoder().encodeToString(bytes);
            groupMemberDesc.setHeadPortrait(headPortrait);
        }
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<List<GroupApplication>> getGroupApplication(User user) {
        List<Group> groupList=infoGetMapper.selectGroupByLeaderAccount(user.getAccount());
        List<GroupApplication> groupApplications = new ArrayList<>();
        for(int m=0;m<groupList.size();m++)
            groupApplications.addAll(infoGetMapper.getGroupApplicationById(groupList.get(m).getGroupId()));
        //注入accountName、applicationName
        for(var groupApplication:groupApplications){
            //accountName
            User accountUser=infoGetMapper.selectByAccount(groupApplication.getAccount());
            groupApplication.setAccountName(accountUser.getNickname());
            //applicantName
            User appicantUser=infoGetMapper.selectByAccount(groupApplication.getApplicantAccount());
            groupApplication.setApplicantName(appicantUser.getNickname());
            //groupName
            GroupDescription groupDescription = infoGetMapper.getGroupInfoById(groupApplication.getGroupId());
            groupApplication.setGroupName(groupDescription.getGroupName());
        }
        groupApplications.sort(Comparator
                .comparingInt((GroupApplication app) -> app.getApplicationStatus() == 0 ? 0 : 1) // 根据 application_status 排序
                .thenComparing(GroupApplication::getApplicationTime) // 根据 application_time 排序
        );
        return ApiResponse.success(groupApplications);
    }

    @Override
    public ApiResponse<List<GroupApplication>> getUserInvitedInfo(User user) {
        List<GroupApplication> groupApplications = infoGetMapper.getGroupApplicationByUser(user.getAccount());
        //注入accountName、applicationName
        for(var groupApplication:groupApplications){
            //accountName
            User accountUser=infoGetMapper.selectByAccount(groupApplication.getAccount());
            groupApplication.setAccountName(accountUser.getNickname());
            //applicantName
            User appicantUser=infoGetMapper.selectByAccount(groupApplication.getApplicantAccount());
            groupApplication.setApplicantName(appicantUser.getNickname());
            //groupName
            GroupDescription groupDescription = infoGetMapper.getGroupInfoById(groupApplication.getGroupId());
            groupApplication.setGroupName(groupDescription.getGroupName());
        }
        groupApplications.sort(Comparator
                .comparingInt((GroupApplication app) -> app.getApplicationStatus() == 0 ? 0 : 1) // 根据 application_status 排序
                .thenComparing(GroupApplication::getApplicationTime) // 根据 application_time 排序
        );
        return ApiResponse.success(groupApplications);
    }


    @Override
    public ApiResponse<List<GroupDescription>> getAllGroups() {
        return ApiResponse.success(infoGetMapper.getAllGroupsInfo());
    }

    @Override
    public ApiResponse<List<GroupMemberDescription>> getAllGroupMembers(int groupId) throws IOException {
        List<GroupMemberDescription> groupMemberDescriptions = new ArrayList<>();
        groupMemberDescriptions = infoGetMapper.getAllGroupMembersInfo(groupId);
        //填入头像
        for(var groupMemberDesc:groupMemberDescriptions){
            User user = infoGetMapper.selectByAccount(groupMemberDesc.getAccount());
            String imgPath;
            imgPath = Paths.get("images/users", user.getHeadPortrait()).toAbsolutePath().toString();
            //传入头像
            final byte[] bytes = IOUtils.toByteArray(
                    new FileInputStream(imgPath));

            String headPortrait = Base64.getEncoder().encodeToString(bytes);
            groupMemberDesc.setHeadPortrait(headPortrait);
        }
        //需要对school字段和role字段做处理
        return ApiResponse.success(groupMemberDescriptions);
    }

    @Override
    public ApiResponse<List<ResourceDescription>> getLibraryInfo(int groupId) {
        //先拿到任务名
        List<Task> tasks = infoGetMapper.selectAllSonTasks();
        List<ResourceDescription> data = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            ResourceDescription description = new ResourceDescription();
            //获取任务名称，都是子任务
            description.setTaskName(tasks.get(i).getTaskName());
            //通过任务Id获得文件信息
            List<Record> records = infoGetMapper.selectRecordByTaskId(tasks.get(i).getTaskId());
            for(var r:records){
                User user = infoGetMapper.selectByAccount(r.getCreatorNickname());
                if(user!=null){
                    r.setCreatorNickname(user.getNickname());
                }
            }
            description.setRecords(records);
            data.add(description);
        }
        ApiResponse<List<ResourceDescription>> response = new ApiResponse<>(true, data, "json");
        return response;
    }

    //获取当前任务列表
    @Override
    public ApiResponse<List<TaskDescription>> getCurrentTaskList(int groupId) {
        List<TaskDescription> taskDescriptions = infoGetMapper.selectAllFatherTasks(groupId);
        for (var fatherTask : taskDescriptions) {
            //获取所有子任务
            List<Task> sonTasks = infoGetMapper.selectSonTask(fatherTask.getTaskId());
            List<TaskDescription> sonTaskList = new ArrayList<>();
            for (var sonTask : sonTasks) {
                TaskDescription sonTaskDescription = new TaskDescription();
                sonTaskDescription.setTaskId(sonTask.getTaskId());
                sonTaskDescription.setTaskName(sonTask.getTaskName());
                sonTaskDescription.setChildrenTask(null);
                sonTaskList.add(sonTaskDescription);
            }
            fatherTask.setChildrenTask(sonTaskList);
        }
        return ApiResponse.success(taskDescriptions);
    }

    @Override
    public ApiResponse<ReplyDescription> getReplyInfoByMessageId(int messageId) throws IOException {

        Message message = infoGetMapper.getMessageByMessageId(messageId);
        ReplyDescription result = new ReplyDescription();

        result.setMessageId(message.getMessageId());
        result.setTitle(message.getTitle());
        result.setContent(message.getMessageContent());
        result.setTaskId(message.getTaskId());
        result.setMessageType(message.getMessageType());
        result.setTime(message.getPostedTime());

        //添加头像
        User user1 = infoGetMapper.selectByAccount(message.getPostAccount());
        String imgPath = "images/users" + message.getHeadPortrait();
        imgPath = Paths.get("images/users", user1.getHeadPortrait()).toAbsolutePath().toString();
        //传入头像
        final byte[] bytes = IOUtils.toByteArray(
                new FileInputStream(new File(imgPath)));
        String headPortrait = Base64.getEncoder().encodeToString(bytes);
        result.setHeadPortrait(headPortrait);
        //添加昵称
        result.setNickname(user1.getNickname());
        //添加taskName
        //当前任务
        Task currentTask = infoGetMapper.selectTaskById(result.getTaskId());
        //有父任务
        if (currentTask.getParentId() != null) {
            String parentName = infoGetMapper.selectTaskById(currentTask.getParentId()).getTaskName();
            String currentName = currentTask.getTaskName();
            result.setTaskName(parentName + ": " + currentName);
        } else//无父任务
        {
            String currentName = currentTask.getTaskName();
            result.setTaskName(currentName);
        }
        //添加回复相关信息
        List<ReplyRecord> replyRecordList=new ArrayList<>();
        //存储原始MessageReply
        List<MessageReply> replyList=infoGetMapper.getReplyByMessageId(result.getMessageId());
        for(int m=0;m<replyList.size();m++)
        {
            ReplyRecord temp=new ReplyRecord();
            //添加头像
            User user2 = infoGetMapper.selectByAccount(replyList.get(m).getReplierAccount());
            String imgPath2 = "images/users" + message.getHeadPortrait();
            imgPath2 = Paths.get("images/users", user2.getHeadPortrait()).toAbsolutePath().toString();
            //传入头像
            final byte[] bytes2 = IOUtils.toByteArray(
                    new FileInputStream(new File(imgPath2)));
            String headPortrait2 = Base64.getEncoder().encodeToString(bytes2);
            temp.setHeadPortrait(headPortrait2);
            //添加昵称
            temp.setNickname(user2.getNickname());
            //添加回复内容
            temp.setReplyContent(replyList.get(m).getReplyContent());
            //添加回复时间
            temp.setReplyTime(replyList.get(m).getReplyTime());
            replyRecordList.add(temp);
        }

        result.setReplyList(replyRecordList);
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<List<Resource>> getResourceInfoByTaskId(int taskId) {
        //if(infoGetMapper.selectTaskById(taskId).getParentId()==null)
        //    return ApiResponse.error("只能是子任务");
        List<ResourceLinker> resourceLinkerList=infoGetMapper.selectLinkerByTaskId(taskId);
        List<Resource>result=new ArrayList<>();
        for(int m=0;m<resourceLinkerList.size();m++)
        {
            result.add(infoGetMapper.selectResourceById(resourceLinkerList.get(m).getResourceId()));
        }
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<GroupDescription> getGroupDescriptionById(int groupId) throws IOException {

        Group group=infoGetMapper.selectGroup(groupId);
        if (group==null)
            return ApiResponse.error("未查到小组");

        GroupDescription result = new GroupDescription();

        result.setGroupId(group.getGroupId());
        result.setGroupName(group.getGroupName());
        result.setState(group.getState());
        result.setDescription(group.getDescription());
        //拿图片
        String imgPath = "";
        imgPath = Paths.get("images/groups", group.getHeadPortrait()).toAbsolutePath().toString();
        //传入头像
        final byte[] bytes = IOUtils.toByteArray(
                new FileInputStream(new File(imgPath)));

        String headPortrait = Base64.getEncoder().encodeToString(bytes);
        result.setHeadPortrait(headPortrait);
        result.setCreateTime(group.getCreateTime());

        User user1 = new User();
        user1.setAccount(group.getLeaderAccount());
        //组长账户
        result.setLeaderAccount(infoGetMapper.selectByAccount(user1.getAccount()).getAccount());
        //组长昵称
        result.setLeaderNickname(infoGetMapper.selectByAccount(user1.getAccount()).getNickname());
        //小组人数
        result.setHeadcount(infoGetMapper.getMemberCount(group.getGroupId()));

        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<Resource> getResourceByMessageId(int messageId) {
        Message message = infoGetMapper.getMessageByMessageId(messageId);
        if(message.getResourceId()==null)
            return ApiResponse.error("无关联资源");
        Resource result=infoGetMapper.selectResourceById(message.getResourceId());
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<List<Task>> getDutyByAccount(String account){
        return ApiResponse.success(infoGetMapper.selectDutyByAccount(account));
    }
}
