package com.fzfnotfound.group.system.entity;


import lombok.Data;

import java.util.List;

@Data
public class TaskDescription {
    private String taskName;
    private int taskId;
    private List<TaskDescription> childrenTask;
}
