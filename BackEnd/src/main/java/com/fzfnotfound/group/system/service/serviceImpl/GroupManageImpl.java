package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.mapper.GroupMapper;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.service.GroupManage;
import com.fzfnotfound.group.system.service.GroupTaskManage;
import com.fzfnotfound.group.system.service.MessageService;
import com.fzfnotfound.group.system.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GroupManageImpl implements GroupManage {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private InfoGetMapper infoGetMapper;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private GroupTaskManage groupTaskManage;

    @Override
    public ApiResponse<String> createGroup(Group group) {
        if(group.getGroupName()==null||group.getGroupName().isEmpty())
            return ApiResponse.error("组名不能为空");
        //设置创建时间
        group.setCreateTime(LocalDateTime.now());
        //创建小组
        groupMapper.createGroup(group);
        //设置资源库文件夹名称，为组名+id
        String resourceDirectory="group"+group.getGroupId();
        group.setGroupResourceLibrary(resourceDirectory);
        //创建文件夹
        groupMapper.updateResourceDir(resourceDirectory, group.getGroupId());
        //添加组长信息
        groupMapper.addGroupLeader(group.getLeaderAccount(),group.getGroupId(),LocalDateTime.now());
        int groupId=group.getGroupId();
        return ApiResponse.success(String.valueOf(groupId));
    }
    //自己申请加入小组
    @Override
    public ApiResponse<String> applyToGroup(GroupApplication groupApplication) {
        //查找是否已申请过
        //无论是自己申请还是别人邀请，都只能存在一个
        //查找是否已申请过
        //无论是自己申请还是别人邀请，都只能存在一个
        List<GroupApplication> result=groupMapper.selectApplicationByAccountAndGroupId(groupApplication);
        if(!result.isEmpty())
            return ApiResponse.error("已存在向该小组的申请");
        //设置申请时间
        groupApplication.setApplicationTime(LocalDateTime.now());
        groupApplication.setApplicationStatus(0);
        groupMapper.addGroupApplication(groupApplication);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> quitGroup(GroupMember groupMember) {
        if(groupMapper.deleteGroupMember(groupMember)==0)
            return ApiResponse.error("数据库删除失败");
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> transferLeader(String presentLeaderAccount, String nextLeaderAccount, Integer groupId) {
        //设置组长和普通成员状态
        groupMapper.setUserLeader(nextLeaderAccount,groupId);
        groupMapper.setUserMember(presentLeaderAccount,groupId);
        //group表改组长account
        groupMapper.setGroupLeader(nextLeaderAccount,groupId);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> updateGroupHeadPortrait(int groupId, MultipartFile file) {
        //存储文件夹
        String baseDir="images/groups";
        String name=file.getOriginalFilename();
        String prefix = name.lastIndexOf(".") != -1 ? name.substring(name.lastIndexOf(".")) : ".jpg";
        String path = UUID.randomUUID().toString().replace("-", "") + prefix;
        try {
            File filePath=new File(baseDir,path);
            //如果用户已经有头像，删除原来的头像
            String existedPath=groupMapper.selectHeadPortraitByGroupId(groupId);
            if(existedPath!=null&&!existedPath.equals("defaultGroup.jpg")){
                File deletePath=new File(baseDir,existedPath);
                if(deletePath.delete()){
                    System.out.println("删除成功");
                }
                else {
                    System.out.println("删除失败");
                }
            }
            //保存头像至文件夹
            file.transferTo(filePath.getAbsoluteFile());
            //保存url
            groupMapper.updateGroupHeadPortrait(groupId,path);
            return ApiResponse.success("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败");
        }
    }

    //删除小组
    @Override
    public ApiResponse<String> deleteGroup(String account, int groupId) {

        System.out.println(groupMapper.selectLeaderAccount(groupId));
        System.out.println(account);
        //如果该组员不是组长
        if(!groupMapper.selectLeaderAccount(groupId).equals(account)){
            return ApiResponse.error("权限不足");
        }
        // 1. 删除group_application相关数据
        groupMapper.deleteApplicaitonByGroupId(groupId);
        //获取小组所有资源
        List<Resource> resourceList=infoGetMapper.getResourceByGroupId(groupId);
        // 2. 删除相关资源(关联删除resource_linker和绑定的留言)
        for(int m=0;m<resourceList.size();m++)
        {
            resourceService.deleteResource(resourceList.get(m).getResourceId());
        }
        //获取小组所有组员
        List<GroupMember> groupMemberList=groupMapper.getGroupMemberList(groupId);
        //获取小组所有任务
        List<Task> taskList=infoGetMapper.selectTaskByGroupId(groupId);
        //获取小组所有留言
        List<Message> messageList=new ArrayList<>();
        for(int m=0;m<taskList.size();m++)
        {
            messageList.addAll(infoGetMapper.getMessageByTaskId(taskList.get(m).getTaskId(),0));
            messageList.addAll(infoGetMapper.getMessageByTaskId(taskList.get(m).getTaskId(),1));
        }
        // 3. 删除小组所有留言(关联删除message_read和message_reply)
        for(int m=0;m<messageList.size();m++)
        {
            messageService.deleteMessage(messageList.get(m).getMessageId());
        }
        // 4. 删除task
        for(int m=0;m<taskList.size();m++)
        {
            groupTaskManage.deleteTaskWhenDeleteGroup(taskList.get(m));
        }
        // 5. 删除group_member
        for(int m=0;m<groupMemberList.size();m++)
        {
            groupMapper.deleteGroupMember(groupMemberList.get(m));
        }

        //删除该组且成功
        if(groupMapper.deleteGroup(groupId)!=0){
            return ApiResponse.success("success");
        }
        return ApiResponse.error("删除失败");
    }

    //删除组员
    @Override
    public ApiResponse<String> removeMember(String account, int groupId) {

        //获得该组的所有成员
        List<GroupMember> groupMembers=new ArrayList<>();
        groupMembers=groupMapper.selectMembersByGroupId(groupId);
        boolean isFind=false;
        //如果组员不在该组
        for (GroupMember groupMember:groupMembers){
            if(groupMember.getAccount()!=null&&groupMember.getAccount().equals(account)){
                isFind=true;
                break;
            }
            System.out.println(groupMember);
        }
        if(!isFind){
            return ApiResponse.error("组员未找到");
        }
        GroupMember groupMember=new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setAccount(account);
        //删除组员
        if(groupMapper.deleteGroupMember(groupMember)!=0){
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    //搜索小组
    @Override
    public ApiResponse<List<Group>> searchGroup(String textInput) {

        return null;
    }

    @Override
    public ApiResponse<String> updateApplicationStatus(String account, int groupId, int status,String reviewer) {
        //修改相关信息
        GroupApplication groupApplication=new GroupApplication();
        groupApplication.setAccount(account);
        groupApplication.setGroupId(groupId);
        groupApplication.setApplicationStatus(status);
        groupApplication.setReviewerAccount(reviewer);
        groupApplication.setReviewTime(LocalDateTime.now());
        groupMapper.updateApplicationStatus(groupApplication);
        //拒绝则只修改status
        if(status==2)
        {
            return ApiResponse.success("success");
        }
        //接受则修改status同时加入小组
        else if(status==1)
        {
            groupMapper.addGroupMember(account,groupId);
            return ApiResponse.success("success");
        }
        else
        {
            return ApiResponse.error("status取值不合法");
        }
    }

    @Override
    public ApiResponse<String> changeGroup(Group group) {
        if(group.getGroupName().isEmpty())
            return ApiResponse.error("小组名不能为空!");
        groupMapper.updateGroup(group);

        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<Integer> changeGroupStatus(Group group) {
        Group realGroup=infoGetMapper.selectGroup(group.getGroupId());
        int toState=(realGroup.getState()==1)?0:1;
        realGroup.setState(toState);
        groupMapper.updateGroupState(realGroup);
        return ApiResponse.success(toState);
    }

}
