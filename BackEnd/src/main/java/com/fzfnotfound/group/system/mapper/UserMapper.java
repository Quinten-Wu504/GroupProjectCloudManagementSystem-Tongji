package com.fzfnotfound.group.system.mapper;


import com.fzfnotfound.group.system.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    //查找某个用户的账户信息
    @Select("select * from user where id= #{id}")
    @Results(value = {
            @Result(column = "last_login",property ="lastLogin",javaType = LocalDateTime.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "date_first_register", property = "updateDate", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP)
    })
    public List<User> selectById(int id);


    //通过用户名查找某个用户的账户信息
    @Select("select * from user where account= #{account}")
    @Results(value = {
            @Result(column = "last_login",property ="lastLogin",javaType = LocalDateTime.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "date_first_register", property = "dateFirstRegister", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP)

    })
    public List<User> selectByAccount(String account);

    //通过手机号查找某个用户的账户信息
    @Select("select * from user where phone_number= #{phoneNumber}")
    @Results(value = {
            @Result(column = "last_login",property ="lastLogin",javaType = LocalDateTime.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "date_first_register", property = "dateFirstRegister", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP)

    })
    public List<User> selectByPhoneNumber(String phoneNumber);



    //默认头像defaultUser.jpg
    //添加某个用户
    @Insert("insert into user(account,password,is_superuser,last_login,email,phone_number,date_first_register,is_active,nickname,head_portrait) values (#{account},#{password},#{isSuperuser},#{lastLogin},#{email}," +
            "#{phoneNumber},#{dateFirstRegister},#{isActive},#{nickname},'defaultUser.jpg')")

    public void addUser(User user);

    //更新用户名
    @Update("update user set nickname = #{nickname} where account = #{account}")
    public void changeName(User user);

    //更新密码
    @Update("update user set password = #{password} where account = #{account}")
    public void changePassword(User user);
    //设置验证码
    @Update("update user set code = #{code} where account = #{account}")
    public void setCode(User user);

    //更新登录时间
    @Update("update user set last_login = #{localDateTime} where account = #{account}")
    public void updateLoginTime(String account,LocalDateTime localDateTime);

    //删除某个用户
    @Delete("delete from user where user.account=#{account}")
    public int deleteAccount(String account);
    //更新用户头像
    @Update("update user set head_portrait = #{path} where account = #{account}")
    public void updateUserHeadPortrait(@Param("account") String account,@Param("path") String path);
    //获取用户头像
    @Select("select head_portrait from user where account = #{account}")
    @Results(value = {
            @Result(column = "head_portrait",property ="headPortrait",javaType = String.class)
    })
    public String selectHeadPortraitByAccount(@Param("account") String account);
    //修改school
    @Update("update user set school = #{school} where account = #{account}")
    public void updateUserSchool(@Param("school")String school,@Param("account")String account);
    //修改school
    @Update("update user set email = #{email} where account = #{account}")
    public void updateUserEmail(@Param("email")String email,@Param("account")String account);
    //根据手机号查用户
    @Select("select * from user where phone_number=#{phoneNumber}")
    public User selectUserByPhoneNumber(@Param("phoneNumber")String phoneNumber);
}
