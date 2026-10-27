package com.fzfnotfound.group.system.entity;

import lombok.Data;

@Data
public class MessageRead {
    private Integer readId;
    private Integer messageId;
    private String account;
}
