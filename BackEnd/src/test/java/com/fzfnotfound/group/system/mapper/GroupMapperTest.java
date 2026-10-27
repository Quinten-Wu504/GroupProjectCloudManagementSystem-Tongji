package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.Group;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupMapperTest {

    @Autowired
    GroupMapper groupMapper;

    @Test
    void createGroup() {
        groupMapper.createGroup(new Group());
    }

    @Test
    void updateResourceDir() {
    }

    @Test
    void addGroupMember() {
    }

    @Test
    void addGroupLeader() {
    }

    @Test
    void addGroupApplication() {
    }

    @Test
    void selectApplicationByAccountAndGroupId() {
    }

    @Test
    void deleteGroupMember() {
    }

    @Test
    void setUserLeader() {
    }

    @Test
    void setUserMember() {
    }

    @Test
    void setGroupLeader() {
    }

    @Test
    void updateGroupHeadPortrait() {
    }

    @Test
    void selectHeadPortraitByGroupId() {
    }

    @Test
    void selectLeaderAccount() {
    }

    @Test
    void deleteGroup() {
    }

    @Test
    void selectMembersByGroupId() {
    }

    @Test
    void updateApplicationStatus() {
    }

    @Test
    void updateGroup() {
    }


}