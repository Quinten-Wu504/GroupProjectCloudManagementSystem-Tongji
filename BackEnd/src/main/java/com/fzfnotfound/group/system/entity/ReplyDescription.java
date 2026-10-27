package com.fzfnotfound.group.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReplyDescription {
    private String headPortrait;
    private String nickname;
    private Integer messageId;
    private String title;
    private String content;
    private Integer taskId;
    private String taskName;
    private Integer messageType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private List<ReplyRecord> replyList;
}
