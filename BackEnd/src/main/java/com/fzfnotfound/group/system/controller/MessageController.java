package com.fzfnotfound.group.system.controller;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Message;
import com.fzfnotfound.group.system.entity.MessageRead;
import com.fzfnotfound.group.system.entity.MessageReply;
import com.fzfnotfound.group.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    //上传留言
    @PostMapping("/uploadMessage")
    public ApiResponse<String> uploadMessage(@RequestBody Message message){
        return messageService.uploadMessage(message);
    }
    //阅读留言(设置已读状态)
    @PostMapping("/readMessage")
    public ApiResponse<String> readMessage(@RequestBody MessageRead messageRead){
        return messageService.readMessage(messageRead);
    }
    //跟帖留言
    @PostMapping("/uploadReply")
    public ApiResponse<String> uploadReply(@RequestBody MessageReply messageReply){
        return messageService.uploadReply(messageReply);
    }
    //设置精华消息
    @PostMapping("/setEssence")
    public ApiResponse<String> setEssence(@RequestBody Message message){
        return messageService.setEssence(message);
    }

    //删除留言
    @PostMapping("/deleteMessage")
    public ApiResponse<String> deleteMessage(@RequestBody Message message){
        if(message.getMessageId()==null)
            System.out.println("NUll!");
        System.out.println(message.getMessageId());
        return messageService.deleteMessage(message.getMessageId());
    }

}
