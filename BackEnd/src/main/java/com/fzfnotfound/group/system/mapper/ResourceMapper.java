package com.fzfnotfound.group.system.mapper;

import com.fzfnotfound.group.system.entity.Resource;
import com.fzfnotfound.group.system.entity.ResourceLinker;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ResourceMapper {

    //上传文件
    @Insert("insert into resource (resource_id, group_id, url, file_type, origin_name, uploader, upload_time) " +
            "values (#{resourceId}, #{groupId}, #{url}, #{fileType}, #{originName}, #{uploader}, #{uploadTime})")
    public void uploadFile(Resource resource);

    //根据url查resource
    @Select("select * from resource where url = #{url}")
    @Results(value = {
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class),
            @Result(column = "group_id",property ="groupId",javaType = Integer.class),
            @Result(column = "file_type",property ="fileType",javaType = Integer.class),
            @Result(column = "origin_name",property ="originName",javaType = String.class),
            @Result(column = "upload_time",property ="uploadTime",javaType = LocalDateTime.class),
    })
    public Resource selectResourceByUrl(@Param("url") String url);

    //上传resource_linker
    @Insert("insert into resource_linker (resource_id, task_id) " +
            "values (#{resourceId}, #{taskId})")
    public void uploadLinker(ResourceLinker resourceLinker);

    //根据resourceId查resource_linker
    @Select("select * from resource_linker where resource_id = #{resourceId}")
    @Results(value = {
            @Result(column = "link_id",property ="linkId",javaType = Integer.class),
            @Result(column = "resource_id",property ="resourceId",javaType = Integer.class),
            @Result(column = "taskId",property ="taskId",javaType = Integer.class),
    })
    public List<ResourceLinker> selectLinkerByResourceId(@Param("resourceId")int resourceId);

    //删除linker
    @Delete("delete from resource_linker where resource_id = #{resourceId} ")
    public int deleteLinker(@Param("resourceId") int resourceId);

    //删除Resource
    @Delete("delete from resource where resource_id = #{resourceId} ")
    public int deleteResource(@Param("resourceId") int resourceId);
}
