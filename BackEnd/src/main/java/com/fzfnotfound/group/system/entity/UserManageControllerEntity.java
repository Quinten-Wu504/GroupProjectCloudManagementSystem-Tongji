package com.fzfnotfound.group.system.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserManageControllerEntity {
    private String account;
    private MultipartFile image;
}
