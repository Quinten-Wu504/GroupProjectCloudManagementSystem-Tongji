package com.fzfnotfound.group.system.entity;


import lombok.Data;

import java.util.List;

@Data
public class ResourceDescription {
    private String taskName;
    private List<Record> records;
}
