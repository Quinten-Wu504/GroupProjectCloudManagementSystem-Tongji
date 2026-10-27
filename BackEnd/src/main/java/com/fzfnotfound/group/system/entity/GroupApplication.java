package com.fzfnotfound.group.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class GroupApplication {
    private Integer applicationId;
    private String account;
    private String accountName;
    private Integer groupId;
    private String groupName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applicationTime;
    private Integer applicationStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;
    private String reviewerAccount;
    private String description;
    private String applicantAccount;
    private String applicantName;

    private Integer status;//仅在controller使用
    private String reviewer;//仅在controller使用
}
