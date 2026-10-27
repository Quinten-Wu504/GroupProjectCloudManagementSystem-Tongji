package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Message;
import com.fzfnotfound.group.system.entity.MessageRead;
import com.fzfnotfound.group.system.entity.MessageReply;

public interface MessageService {
    public ApiResponse<String> uploadMessage(Message message);

    public ApiResponse<String> readMessage(MessageRead messageRead);

    public ApiResponse<String> uploadReply(MessageReply messageReply);

    public ApiResponse<String> setEssence(Message message);

    public ApiResponse<String> deleteMessage(int messageId);
}
