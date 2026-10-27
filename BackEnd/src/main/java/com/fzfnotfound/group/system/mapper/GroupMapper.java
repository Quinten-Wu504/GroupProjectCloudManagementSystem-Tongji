package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.Group;
import com.fzfnotfound.group.system.entity.GroupApplication;
import com.fzfnotfound.group.system.entity.GroupMember;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GroupMapper {
    //创建小组
    @Insert("insert into group_ (group_name,description,leader_account,create_time,head_portrait,state)" +
            " values (#{groupName},#{description},#{leaderAccount},#{createTime},'defaultGroup.jpg',1)")
    @SelectKey(statement="select LAST_INSERT_ID()", keyProperty="groupId", before=false, resultType = int.class)
    @Options(useGeneratedKeys = true,keyColumn = "group_id",keyProperty = "groupId")
    public void createGroup(Group group);

    //更新资源库目录
    @Update("update group_ set group_resource_library=#{groupResourceLibrary} where group_id=#{groupId}  ")
    public void updateResourceDir(@Param("groupResourceLibrary") String dir,int groupId);


    //@ToDo
    //这两个为啥不写成一个呢？把role改了就好了
    //添加成员
    //未添加加入时间
    @Insert("insert into group_member (account,group_id,role) values (#{account}, #{groupId}, 1)")
    public void addGroupMember(@Param("account") String account,@Param("groupId") int groupId);

    //创建小组时添加组长
    @Insert("insert into group_member (account,group_id,role,enter_time) values" +
            " (#{account}, #{groupId}, 0,#{enterTime})")
    public void addGroupLeader(@Param("account") String account, @Param("groupId") int groupId, @Param("enterTime")LocalDateTime localDateTime);

    //申请加入小组，加入审批列表
    //status=0代表未审核，1通过，2未通过
    @Insert("insert into group_application (account, group_id,application_status,application_time,description,applicant_account)" +
            " values (#{account}, #{groupId},0,#{applicationTime},#{description},#{applicantAccount})")
    public void addGroupApplication(GroupApplication groupApplication);

    //根据account和groupId查找审批列表
    //status=0代表未审核，1通过，2未通过
    @Select("select * from group_application where account=#{account} and group_id = #{groupId} and application_status = 0")
    public List<GroupApplication> selectApplicationByAccountAndGroupId(GroupApplication groupApplication);

    //退出小组，group_member表中删除对应项
    @Delete("delete from group_member where account=#{account} and group_id=#{groupId}")
    public int deleteGroupMember(GroupMember groupMember);

    //设置用户为组长
    @Update("update group_member set role = 0 where account = #{account} and group_id = #{groupId}")
    public void setUserLeader(@Param("account") String account,@Param("groupId") int groupId);

    //设置用户为成员
    @Update("update group_member set role = 1 where account = #{account} and group_id = #{groupId}")
    public void setUserMember(@Param("account") String account,@Param("groupId") int groupId);

    //更改group表小组组长
    @Update("update group_ set leader_account = #{account} where group_id = #{groupId}")
    public void setGroupLeader(@Param("account") String account, @Param("groupId")int groupId);

    //更新小组头像
    @Update("update group_ set head_portrait = #{path} where group_id = #{groupId}")
    public void updateGroupHeadPortrait(@Param("groupId")int groupId,@Param("path")String path);
    //获取用户头像
    @Select("select head_portrait from group_ where group_id = #{groupId}")
    public String selectHeadPortraitByGroupId(@Param("groupId")int groupId);
    //获取小组组长账号
    @Select("select leader_account from group_ where group_id = #{groupId}")
    public String selectLeaderAccount(@Param("groupId") int groupId);
    //解散小组
    @Delete("delete from group_ where group_id = #{groupId}")
    public int deleteGroup(@Param("groupId") int groupId);

    //获取所有小组成员
    //只是GroupMember形式
    @Select("select m.record_id,m.account,m.group_id,m.role,m.enter_time " +
            " from group_ as g join group_member as m on g.group_id = m.group_id" +
            " where g.group_id= #{groupId} ")
    public List<GroupMember> selectMembersByGroupId(@Param("groupId") int groupId);

    //修改application_status
    @Update("update group_application set application_status = #{applicationStatus}, reviewer_account=#{reviewerAccount}, review_time = #{reviewTime} " +
            "where account=#{account} and group_id = #{groupId}")
    public void updateApplicationStatus(GroupApplication groupApplication);

    //修改小组信息
    @Update("update group_ set group_name= #{groupName},description = #{description} where group_id = #{groupId}")
    public void updateGroup(Group group);

    //设置小组状态
    @Update("update group_ set state=#{state} where group_id=#{groupId}")
    public void updateGroupState(Group group);

    //根据groupId删application
    @Delete("delete from group_application where group_id = #{groupId}")
    public void deleteApplicaitonByGroupId(@Param("groupId")int groupId);

    @Select("select * from group_member where group_id = #{groupId}")
    public List<GroupMember> getGroupMemberList(@Param("groupId")int groupId);
}
