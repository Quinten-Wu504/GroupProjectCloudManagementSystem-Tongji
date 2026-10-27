package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Resource;
import com.fzfnotfound.group.system.entity.ResourceLinker;
import com.fzfnotfound.group.system.entity.Task;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.mapper.ResourceMapper;
import com.fzfnotfound.group.system.mapper.TaskMapper;
import com.fzfnotfound.group.system.service.GroupTaskManage;
import com.fzfnotfound.group.system.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupTaskManageImpl implements GroupTaskManage {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private InfoGetMapper infoGetMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private ResourceService resourceService;

    @Override
    public ApiResponse<String> createTask(Task task) {
        if(task.getTaskName()==null)
            return ApiResponse.error("任务名不能为空");
        if(task.getDescription()==null)
            return ApiResponse.error("任务描述不能为空");
        taskMapper.createTask(task);
        int taskId=task.getTaskId();
        return ApiResponse.success(String.valueOf(taskId));
    }

    @Override
    public ApiResponse<String> addTask(Task task) {
        List<Task> TopActiveTask=infoGetMapper.selectTopActiveTask(infoGetMapper.selectTaskById(task.getTaskId()).getGroupId());
        boolean have_active_parent= false;
        for(int m=0;m<TopActiveTask.size();m++)
        {
            if(task.getParentId()==TopActiveTask.get(m).getTaskId())
            {
                have_active_parent=true;
                break;
            }
        }
        if(task.getParentId()!=null&&!have_active_parent)
            return ApiResponse.error("填写的父任务不存在或尚未启用");
        if (task.getDeadline() == null)
            return ApiResponse.error("截止日期不能为空");
        if (task.getStartTime() == null)
            return ApiResponse.error("开始时间不能为空");
        if(task.getAdvanceTime()==null)
            return ApiResponse.error("提前通知时间不能为空");
        if(task.getResponsible()==null)
            return ApiResponse.error("主负责人不能为空");


        taskMapper.setTaskOpen(task);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> deleteTask(Task task) {
        if(infoGetMapper.selectTaskById(task.getTaskId())==null)
            return ApiResponse.error("不存在该任务");
        if(infoGetMapper.selectTaskById(task.getTaskId()).getStatus()!=0)
            return ApiResponse.error("只能删除未启用的任务");

        //删除关联文件
        List<ResourceLinker> resourceList=infoGetMapper.selectLinkerByTaskId(task.getTaskId());
        for(int m=0;m<resourceList.size();m++)
        {
            resourceService.deleteResource(resourceList.get(m).getResourceId());
        }
        taskMapper.DeleteTask(task);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> taskSetNotActive(Task task) {
        List<Task> temp=new ArrayList<>();
        temp.addAll(taskMapper.selectSonTask(task.getTaskId()));
        //移除与子任务的父子关系
        for(int m=0;m<temp.size();m++)
        {
            taskMapper.changeTaskParentIdToNULL(temp.get(m).getTaskId());
        }
        taskMapper.setTaskNotActive(task);
        taskMapper.changeTaskParentIdToNULL(task.getTaskId());
        taskMapper.changeAdvanceTimeToNULL(task.getTaskId());
        taskMapper.changeDeadlineToNULL(task.getTaskId());
        taskMapper.changeResponsibleToNULL(task.getTaskId());
        taskMapper.changeStartTimeToNULL(task.getTaskId());
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> changeAlternativeTask(Task task) {
        if(task.getTaskName()!=null)
            taskMapper.changeTaskName(task.getTaskId(),task.getTaskName());
        if(task.getDescription()!=null)
           taskMapper.changeTaskDescription(task.getTaskId(),task.getDescription());
        if(task.getParentId()!=null)
           taskMapper.changeTaskParentId(task.getTaskId(),task.getParentId());
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> changeActiveTask(Task task) {
        //任务名字
        if(task.getTaskName()!=null)
            taskMapper.changeTaskName(task.getTaskId(),task.getTaskName());
        //任务描述
        if(task.getDescription()!=null)
            taskMapper.changeTaskDescription(task.getTaskId(),task.getDescription());
        //deadline
        if(task.getDeadline()!=null)
            taskMapper.changeTaskDeadline(task.getTaskId(),task.getDeadline());
        //start_time
        if(task.getStartTime()!=null)
            taskMapper.changeTaskStartTime(task.getTaskId(),task.getStartTime());
        //advance_time
        if(task.getAdvanceTime()!=null)
            taskMapper.changeTaskAdvanceTime(task.getTaskId(),task.getAdvanceTime());
        //responsible
        if(task.getResponsible()!=null)
            taskMapper.changeTaskResponsible(task.getTaskId(),task.getResponsible());
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> deleteTaskWhenDeleteGroup(Task task){
        List<Task> sonTaskList=infoGetMapper.selectSonTask(task.getTaskId());
        for(int m=0;m<sonTaskList.size();m++)
        {
            taskMapper.DeleteTask(sonTaskList.get(m));
        }
        taskMapper.DeleteTask(task);
        return ApiResponse.success("success");
    }
}
