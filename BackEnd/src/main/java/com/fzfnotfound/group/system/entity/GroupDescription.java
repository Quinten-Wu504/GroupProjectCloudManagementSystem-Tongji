package com.fzfnotfound.group.system.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class GroupDescription {
    private Integer groupId;
    private String groupName;
    private String leaderAccount;
    private String leaderNickname;
    private int headcount;
    private Integer state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String headPortrait;
    private String description;
}
