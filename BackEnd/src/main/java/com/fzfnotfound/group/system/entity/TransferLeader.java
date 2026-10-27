package com.fzfnotfound.group.system.entity;

import lombok.Data;

@Data
public class TransferLeader {
    private String presentLeaderAccount;
    private String nextLeaderAccount;
    private Integer groupId;
}
