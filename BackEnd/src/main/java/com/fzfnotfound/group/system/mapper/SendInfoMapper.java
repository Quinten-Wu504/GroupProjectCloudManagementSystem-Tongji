package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.DeadlineInfo;
import com.fzfnotfound.group.system.entity.SendInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SendInfoMapper {



    @Select("""   
    SELECT
    DISTINCT group_name, task_name,deadline,advance_time
    FROM
    	group_member AS gm
    	JOIN group_ AS g ON gm.group_id = g.group_id JOIN task as t ON gm.group_id=t.group_id
    	
    	WHERE gm.account=#{account} and t.status=1
    """)
    @Results(value = {
            @Result(column = "group_name",property ="groupName",javaType = String.class),
            @Result(column = "task_name",property ="taskName",javaType = String.class),
            @Result(column = "deadline",property ="deadline",javaType = LocalDateTime.class),
            @Result(column = "advance_time",property = "advanceTime")
    })

    List<DeadlineInfo> selectGroupAndTaskInfoByAccount(String account);
}
