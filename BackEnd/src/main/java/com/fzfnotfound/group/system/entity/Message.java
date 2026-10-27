package com.fzfnotfound.group.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class Message {
    private Integer messageId;
    private Integer taskId;
    private String postAccount;
    private String messageContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postedTime;
    private Integer messageType;
    private Integer resourceId;
    private String title;
    private String headPortrait;//仅用作返回
}
