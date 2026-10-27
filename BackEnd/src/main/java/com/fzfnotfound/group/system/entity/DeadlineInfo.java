package com.fzfnotfound.group.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeadlineInfo {
    private String groupName;
    private String taskName;
    private LocalDateTime deadline;
    //单位是天
    private Integer advanceTime;
}
