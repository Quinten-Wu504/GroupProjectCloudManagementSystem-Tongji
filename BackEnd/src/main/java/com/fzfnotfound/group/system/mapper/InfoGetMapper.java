package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.*;
import com.fzfnotfound.group.system.entity.Record;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InfoGetMapper {

    //根据account查用户信息
    @Select("select * from user where account= #{account}")
    @Results(value = {
            @Result(column = "is_superuser",property ="isSuperuser",javaType = Integer.class),
            @Result(column = "last_login",property ="lastLogin",javaType = LocalDateTime.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "phone_number",property ="phoneNumber",javaType = String.class),
            @Result(column = "date_first_register", property = "dateFirstRegister", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "is_active",property ="isActive",javaType = Integer.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class)
    })
    public User selectByAccount(@Param("account") String account);
    //查用户加入的小组id
    @Select("select * from group_member where account=#{account}")
    @Results(value = {
            @Result(column = "group_id",property ="groupId",javaType = Integer.class)
    })
    public List<GroupMember> selectEnteredGroupId(User user);
    //根据groupId查小组信息
    @Select("select * from group_ where group_id = #{groupId}")
    @Results(value = {
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "group_name",property ="groupName",javaType = String.class),
            @Result(column = "leader_account",property ="leaderAccount",javaType = String.class),
            @Result(column = "group_resource_library",property ="groupResourceLibrary",javaType = String.class),
            @Result(column = "create_time",property ="createTime",javaType = LocalDateTime.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class)
    })
    public Group selectGroup(@Param("groupId") Integer groupId);
    //根据groupId查备选一级任务列表
    @Select("select * from task where group_id = #{groupId} and status = 0 and parent_id is NULL")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectAlternativeTask(@Param("groupId") Integer groupId);
    //根据groupId查活跃任务列表
    @Select("select * from task where group_id = #{groupId} and status = 1")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectActiveTask(@Param("groupId") Integer groupId);


    //根据groupId查活跃一级任务列表
    @Select("select * from task where group_id = #{groupId} and status = 1 and parent_id is NULL")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectTopActiveTask(@Param("groupId") Integer groupId);

    //获取指定父任务的子任务
    @Select("select * from task where parent_id=#{taskId}")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectSonTask(@Param("taskId")Integer taskId);

    //根据messageId查留言
    @Select("select * from message where message_id=#{messageId}")
    @Results(value = {
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "post_account",property ="postAccount",javaType = String.class),
            @Result(column = "message_content",property ="messageContent",javaType = String.class),
            @Result(column = "posted_time",property ="postedTime",javaType = LocalDateTime.class),
            @Result(column = "message_type",property ="messageType",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class)
    })
    public Message getMessageByMessageId(@Param("messageId")Integer messageId);

    //根据taskId查留言
    @Select("select * from message where task_id=#{taskId} and message_type =#{messageType}")
    @Results(value = {
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "post_account",property ="postAccount",javaType = String.class),
            @Result(column = "message_content",property ="messageContent",javaType = String.class),
            @Result(column = "posted_time",property ="postedTime",javaType = LocalDateTime.class),
            @Result(column = "message_type",property ="messageType",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class)
    })
    public List<Message> getMessageByTaskId(@Param("taskId")Integer taskId, @Param("messageType")Integer messageType);

    //根据resourceId查留言
    @Select("select * from message where resource_id=#{resourceId} and message_type =#{messageType}")
    @Results(value = {
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "post_account",property ="postAccount",javaType = String.class),
            @Result(column = "message_content",property ="messageContent",javaType = String.class),
            @Result(column = "posted_time",property ="postedTime",javaType = LocalDateTime.class),
            @Result(column = "message_type",property ="messageType",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class)
    })
    public List<Message> getMessageByResourceId(@Param("resourceId")Integer resourceId, @Param("messageType")Integer messageType);

    //根据resourceId查仅关联resource的留言
    @Select("select * from message where resource_id=#{resourceId} and message_type =#{messageType} and task_id is NULL")
    @Results(value = {
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "post_account",property ="postAccount",javaType = String.class),
            @Result(column = "message_content",property ="messageContent",javaType = String.class),
            @Result(column = "posted_time",property ="postedTime",javaType = LocalDateTime.class),
            @Result(column = "message_type",property ="messageType",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class)
    })
    public List<Message> getResourceOnlyMessageByResourceId(@Param("resourceId")Integer resourceId, @Param("messageType")Integer messageType);

    //查小组的所有资源
    @Select("select * from resource where group_id = #{groupId}")
    @Results(value = {
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class)
    })
    public List<Resource> getResourceByGroupId(@Param("groupId")Integer groupId);



    //获取小组的全部组员
    @Select("select * from group_ as g join group_member as m on g.group_id = m.group_id")

    public List<User> getGroupMembersByGroupId(@Param("groupId")int groupId);


    //获取所有用户信息
    @Select("""
    select * from user where account in 
    ((select account from user) except (select account from group_member where group_id =#{groupId}))
    """
    )
    @Results(value = {
            @Result(column = "is_superuser",property ="isSuperuser",javaType = Integer.class),
            @Result(column = "last_login",property ="lastLogin",javaType = LocalDateTime.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "phone_number",property ="phoneNumber",javaType = String.class),
            @Result(column = "date_first_register", property = "dateFirstRegister", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "is_active",property ="isActive",javaType = Integer.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class)
    })
    public List<User> selectAllUsers(int groupId);

    //获取小组的申请加入信息（不包括邀请加入）
    @Select("""
            select * from group_application where group_id=#{groupId} and account = applicant_account
            """)
    @Results(value = {
            @Result(column = "account",property ="account",javaType = String.class),
            @Result(column = "application_id",property ="applicationId",javaType = Integer.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "application_time",property ="applicationTime",javaType = LocalDateTime.class),
            @Result(column = "application_status",property ="applicationStatus",javaType = Integer.class),
            @Result(column = "review_time",property ="reviewTime",javaType = LocalDateTime.class),
            @Result(column = "reviewer_account",property ="reviewerAccount",javaType = String.class),
            @Result(column = "applicant_account",property ="applicantAccount",javaType = String.class),
            @Result(column = "description",property ="description",javaType = String.class),
    })
    public List<GroupApplication> getGroupApplicationById(@Param("groupId") int groupId);

    //获取个人被邀请加入小组的信息
    @Select("select * from group_application where account=#{account} and account != applicant_account")
    @Results(value = {
            @Result(column = "application_id",property ="applicationId",javaType = Integer.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "application_time",property ="applicationTime",javaType = LocalDateTime.class),
            @Result(column = "application_status",property ="applicationStatus",javaType = Integer.class),
            @Result(column = "review_time",property ="reviewTime",javaType = LocalDateTime.class),
            @Result(column = "reviewer_account",property ="reviewerAccount",javaType = String.class),
            @Result(column = "applicant_account",property ="applicantAccount",javaType = String.class)
    })
    public List<GroupApplication> getGroupApplicationByUser(@Param("account") String account);


    @Select("select group_name,headcount,nickname,state, create_time, sub.head_portrait from" +
            "(select g.group_name as group_name, count(*) as headcount,g.head_portrait," +
            "g.leader_account as leader_account, g.state as state,g.create_time as create_time " +
            "from group_ as g join group_member as gm on g.group_id = gm.group_id " +
            "group by g.group_id) as sub join user on sub.leader_account = user.account"
            )
    @Results(value = {
            @Result(column = "group_name",property ="groupName",javaType = String.class),
            @Result(column = "headcount",property ="headcount",javaType = Integer.class),
            @Result(column = "nickname",property ="leaderNickname",javaType = String.class),
            @Result(column = "state",property ="state",javaType = String.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class),
            @Result(column = "create_time",property ="createTime",javaType = LocalDateTime.class),

    })
    public List<GroupDescription> getAllGroupsInfo();


    @Select("""
        select group_name,description,head_portrait,leader_account from group_ where group_id = #{groupId}
"""
    )
    @Results(value = {
            @Result(column = "group_name",property ="groupName",javaType = String.class),
            @Result(column = "description",property ="description",javaType = String.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class),
            @Result(column = "leader_account",property ="leaderAccount",javaType = String.class),
    })
    public GroupDescription getGroupInfoById(int groupId);


    @Select("""
SELECT
	sub.account,
	nickname,
	email,
	role,
	enter_time
FROM
	(
	SELECT
		account,
		enter_time,
		role
	FROM
		group_ AS g
		JOIN group_member AS gm ON g.group_id = gm.group_id
	WHERE
		g.group_id = #{groupId}
	) AS sub
	JOIN user ON sub.account = user.account
"""
    )
    @Results(value = {
            @Result(column = "nickname",property ="nickname",javaType = String.class),
            @Result(column = "email",property = "email",javaType = String.class),
            @Result(column = "role",property ="role",javaType = String.class),
            @Result(column = "enter_time",property ="enterTime",javaType = LocalDateTime.class),
            @Result(column = "account",property = "account",javaType = String.class)

    })
    public List<GroupMemberDescription> getAllGroupMembersInfo(int groupId);


    @Select("select count(*) as count from group_member where group_id = #{groupId}")
    public int getMemberCount(@Param("groupId") int groupId);

    @Select("select * from task where parent_id is NOT NULL")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public  List<Task> selectAllSonTasks();

    @Select("select task_id,task_name from task where parent_id is NULL and group_id = #{groupId}")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
    })
    public List<TaskDescription> selectAllFatherTasks(int groupId);




    @Select("""
            SELECT
                m.resource_id,
                origin_name,
                uploader,
                upload_time,
                message_content
             FROM
                task AS t
                JOIN resource_linker AS rl ON t.task_id = rl.task_id
                JOIN resource AS r ON rl.resource_id = r.resource_id
                JOIN message AS m ON r.resource_id = m.resource_id
             WHERE
                t.task_id = #{taskId}
             """)
    @Results(value = {
            @Result(column = "resource_id",property ="resourceId"),
            @Result(column = "origin_name",property ="fileName",javaType = String.class),
            @Result(column = "uploader",property ="creatorNickname",javaType = String.class),
            @Result(column = "upload_time",property ="createTime",javaType = LocalDateTime.class),
            @Result(column = "message_content",property ="description",javaType = String.class),
    })
    public List<Record> selectRecordByTaskId(int taskId);

    @Select("select * from task where task_id = #{taskId}")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public Task selectTaskById(@Param("taskId")int taskId);

    @Select("select count(*) as count from message_reply where message_id = #{messageId}")
    public int getReplyCount(@Param("messageId") int messageId);

    //根据messageId选择message_reply
    @Select("select * from message_reply where message_id=#{messageId}")
    @Results(value = {
            @Result(column = "reply_id",property ="replyId",javaType = Integer.class),
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
            @Result(column = "replier_account",property ="replierAccount",javaType = String.class),
            @Result(column = "reply_content",property ="replyContent",javaType = String.class),
            @Result(column = "reply_time",property ="replyTime",javaType = LocalDateTime.class)
    })
    public List<MessageReply> getReplyByMessageId(@Param("messageId")int messageId);

    //根据resourceId查resource
    @Select("select * from resource where resource_id = #{resourceId}")
    @Results(value = {
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "file_type",property ="fileType",javaType = Integer.class),
            @Result(column = "origin_name",property ="originName",javaType = String.class),
            @Result(column = "upload_time",property ="uploadTime",javaType = LocalDateTime.class),
    })
    public Resource selectResourceById(@Param("resourceId") int resourceId);

    //根据taskId查resource_linker
    @Select("select * from resource_linker where task_id = #{taskId}")
    @Results(value = {
            @Result(column = "link_id",property ="linkId",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class),
            @Result(column = "taskId",property ="taskId",javaType = Integer.class),
    })
    public List<ResourceLinker> selectLinkerByTaskId(@Param("taskId")int taskId);

    //根据account查自己是组长的group
    @Select("select * from group_ where leader_account = #{leaderAccount}")
    @Results(value = {
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "group_name",property ="groupName",javaType = String.class),
            @Result(column = "leader_account",property ="leaderAccount",javaType = String.class),
            @Result(column = "group_resource_library",property ="groupResourceLibrary",javaType = String.class),
            @Result(column = "create_time",property ="createTime",javaType = LocalDateTime.class),
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class)
    })
    public List<Group> selectGroupByLeaderAccount(@Param("leaderAccount")String leaderAccount);

    //根据groupId查所有任务
    @Select("select * from task where group_id=#{groupId}")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectTaskByGroupId(@Param("groupId")int groupId);

    //根据account拿负责的task
    @Select("select * from task where responsible=#{responsible}")
    @Results(value = {
            @Result(column = "task_id",property ="taskId",javaType = Integer.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "creator_account",property ="creatorAccount",javaType = String.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "start_time",property ="startTime",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property ="advanceTime",javaType = Integer.class),
            @Result(column = "parent_id",property ="parentId",javaType = Integer.class)
    })
    public List<Task> selectDutyByAccount(@Param("responsible")String responsible);
}


