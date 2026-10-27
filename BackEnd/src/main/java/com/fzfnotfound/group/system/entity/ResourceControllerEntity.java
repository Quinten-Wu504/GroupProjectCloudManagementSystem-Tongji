package com.fzfnotfound.group.system.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceControllerEntity {
    private Integer groupId;
    private MultipartFile file;
    private Integer taskId;
    private String account;
    private String description;
    private Integer resourceId;
}
