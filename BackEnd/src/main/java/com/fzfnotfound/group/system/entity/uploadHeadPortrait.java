package com.fzfnotfound.group.system.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class uploadHeadPortrait {
    private Integer groupId;
    private MultipartFile image;
}
