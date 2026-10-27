package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {
    //创建任务
    @Insert("insert into task (task_name, creator_account, group_id, status, description) " +
            "values (#{taskName}, #{creatorAccount}, #{groupId}, 0, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "taskId", keyColumn = "task_id")
    public int createTask(Task task);

    //任务状态设为启用
    @Update("update task " +
            "set status=1, deadline=#{deadline}, start_time=#{startTime}, " +
            "advance_time=#{advanceTime}, responsible=#{responsible}, parent_id = #{parentId}, description = #{description} where task_id=#{taskId} ")
    public void setTaskOpen(Task task);

    //任务状态取消启用
    @Update("update task set status=0,parent_id = NULL where task_id=#{taskId} ")
    public void setTaskNotActive(Task task);

    //删除任务
    @Delete("delete from task where task_id=#{taskId}")
    public int DeleteTask(Task task);

    //根据id搜索任务
    @Select("select * from task where task_id=#{taskId}")
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

    //根据taskId查所有直接子任务
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

    //更改任务名字
    @Update("update task set task_name = #{taskName} where task_id=#{taskId}")
    public void changeTaskName(@Param("taskId")int taskId, @Param("taskName") String taskName);

    //更改任务描述
    @Update("update task set description = #{description} where task_id=#{taskId}")
    public void changeTaskDescription(@Param("taskId")int taskId, @Param("description") String description);

    //更改deadline
    @Update("update task set deadline = #{deadline} where task_id=#{taskId}")
    public void changeTaskDeadline(@Param("taskId")int taskId, @Param("deadline") LocalDateTime deadline);

    //更改start_time
    @Update("update task set start_time = #{startTime} where task_id=#{taskId}")
    public void changeTaskStartTime(@Param("taskId")int taskId, @Param("startTime") LocalDateTime startTime);

    //更改advance_time
    @Update("update task set advance_time = #{advanceTime} where task_id=#{taskId}")
    public void changeTaskAdvanceTime(@Param("taskId")int taskId, @Param("advanceTime") int advanceTime);

    //更改parent_id
    @Update("update task set parent_id = #{parentId} where task_id=#{taskId}")
    public void changeTaskParentId(@Param("taskId")int taskId, @Param("parentId") int parentId);

    //修改主负责人
    @Update("update task set responsible = #{responsible} where task_id=#{taskId}")
    public void changeTaskResponsible(@Param("taskId")int taskId, @Param("responsible") String responsible);

    //更改parent_id为NULL
    @Update("update task set parent_id = NULL where task_id=#{taskId}")
    public void changeTaskParentIdToNULL(@Param("taskId")int taskId);

    //更改responsible为NULL
    @Update("update task set responsible = NULL where task_id=#{taskId}")
    public void changeResponsibleToNULL(@Param("taskId")int taskId);

    //更改deadline为NULL
    @Update("update task set deadline = NULL where task_id=#{taskId}")
    public void changeDeadlineToNULL(@Param("taskId")int taskId);

    //更改start_time为NULL
    @Update("update task set start_time = NULL where task_id=#{taskId}")
    public void changeStartTimeToNULL(@Param("taskId")int taskId);

    //更改advance_time为NULL
    @Update("update task set advance_time = NULL where task_id=#{taskId}")
    public void changeAdvanceTimeToNULL(@Param("taskId")int taskId);
}
