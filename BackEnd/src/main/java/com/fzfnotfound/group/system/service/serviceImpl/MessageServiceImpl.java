package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.mapper.MessageMapper;
import com.fzfnotfound.group.system.mapper.ResourceMapper;
import com.fzfnotfound.group.system.service.InfoGet;
import com.fzfnotfound.group.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private InfoGetMapper infoGetMapper;

    @Override
    public ApiResponse<String> uploadMessage(Message message) {
        if(message.getMessageContent()==null)
            return ApiResponse.error("留言内容不能为空");
        if(message.getPostAccount()==null)
            return ApiResponse.error("发布者不能为空");
        if(message.getTaskId()==null&&message.getResourceId()==null)
            return ApiResponse.error("必须关联任务或资源");
        //设置发布时间
        message.setPostedTime(LocalDateTime.now());
        messageMapper.uploadMessage(message);
        int messageId=message.getMessageId();
        return ApiResponse.success(String.valueOf(messageId));
    }

    @Override
    public ApiResponse<String> readMessage(MessageRead messageRead) {
        if(messageRead.getMessageId()==null)
            return ApiResponse.error("阅读的留言id不能为空");
        if(messageRead.getAccount()==null)
            return ApiResponse.error("阅读者账户不能为空");
        messageMapper.setReaded(messageRead);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> uploadReply(MessageReply messageReply) {
        if(messageReply.getMessageId()==null)
            return ApiResponse.error("目标回复留言不能为空");
        if(messageReply.getReplyContent()==null)
            return ApiResponse.error("回复内容不能为空");
        if(messageReply.getReplierAccount()==null)
            return ApiResponse.error("回复者账户不能为空");
        messageReply.setReplyTime(LocalDateTime.now());
        messageMapper.uploadReply(messageReply);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> setEssence(Message message) {
        if(message.getMessageId()==null)
            return ApiResponse.error("留言id不能为空");
        if(message.getMessageType()==null)
            return ApiResponse.error("留言状态不能为空");
        messageMapper.setEssence(message);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> deleteMessage(int messageId) {
        //获取资源id
        Message message=messageMapper.selectResourceIdById(messageId);
        boolean have_resource=true;
        if(message.getResourceId()==null)
            have_resource=false;
        //先删除message_read
        messageMapper.deleteMessageReadByMessageId(messageId);
        //删除message_reply
        messageMapper.deleteReplyByMessageId(messageId);

        if(messageMapper.deleteMessage(messageId)==0){
            return ApiResponse.error("未找到留言记录");
        }
        if(have_resource) {
            int resourceId = message.getResourceId();
            Resource resource = infoGetMapper.selectResourceById(resourceId);
            String baseDir = "resources/" + infoGetMapper.selectGroup(resource.getGroupId()).getGroupResourceLibrary();
            String existedPath = resource.getUrl();
            //删除相应资源
            //删除resource_linker
            resourceMapper.deleteLinker(resourceId);
            //删除resource
            resourceMapper.deleteResource(resourceId);
            //实际删除文件
            if (existedPath != null) {
                File deletePath = new File(baseDir, existedPath);
                if (deletePath.delete()) {
                    System.out.println("删除成功");
                } else {
                    System.out.println("删除失败");
                }
            }
        }

        return ApiResponse.success("success");
    }
}
