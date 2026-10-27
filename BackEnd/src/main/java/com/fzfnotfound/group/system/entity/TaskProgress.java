package com.fzfnotfound.group.system.entity;

import lombok.Data;

@Data
public class TaskProgress {
    private Integer progressId;
    private Integer taskId;
    private String updaterAccount;
    private String progressDiscription;
    private Double percent;
}
