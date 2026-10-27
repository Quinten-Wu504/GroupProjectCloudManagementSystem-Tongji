package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.Message;
import com.fzfnotfound.group.system.entity.MessageRead;
import com.fzfnotfound.group.system.entity.MessageReply;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MessageMapper {
    //type插入默认为0，代表非精华消息
    @Insert("insert into message (task_id, post_account, message_content, message_type, posted_time,title,resource_id) " +
            "values (#{taskId}, #{postAccount}, #{messageContent}, #{messageType}, #{postedTime},#{title},#{resourceId})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId", keyColumn = "message_id")
    public void uploadMessage(Message message);

    //设置为某账户已读
    @Insert("insert into message_read (message_id, account) " +
            "values (#{messageId}, #{account})")
    public void setReaded(MessageRead messageRead);

    //回复留言
    @Insert("insert into message_reply (message_id, replier_account, reply_content,reply_time) " +
            "values (#{messageId}, #{replierAccount}, #{replyContent},#{replyTime})")
    public void uploadReply(MessageReply messageReply);

    //设置消息状态
    @Update("update message set message_type = #{messageType} where message_id=#{messageId}")
    public void setEssence(Message message);

    //删除留言
    @Delete("delete from message where message_id = #{messageId} ")
    public int deleteMessage(@Param("messageId") int messageId);

    @Select("select * from message where message_id =#{messageId}")
    @Results(value = {
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class),
    })
    public Message selectResourceIdById(@Param("messageId") int messageId);


    @Select("select * from message_read where message_id=#{messageId}")
    @Results(value = {
            @Result(column = "read_id",property ="readId",javaType = Integer.class),
            @Result(column = "message_id",property ="messageId",javaType = Integer.class),
    })
    public List<MessageRead> selectMessageReadByMessageId(@Param("messageId")int messageId);

    @Delete("delete from message_read where message_id = #{messageId}")
    public void deleteMessageReadByMessageId(@Param("messageId")int messageId);

    @Delete("delete from message_reply where message_id =#{messageId}")
    public void deleteReplyByMessageId(@Param("messageId")int messageId);
}
