package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Message;
import com.fzfnotfound.group.system.entity.Resource;
import com.fzfnotfound.group.system.entity.ResourceLinker;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.mapper.MessageMapper;
import com.fzfnotfound.group.system.mapper.ResourceMapper;
import com.fzfnotfound.group.system.mapper.TaskMapper;
import com.fzfnotfound.group.system.service.ResourceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private InfoGetMapper infoGetMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public ApiResponse<String> uploadResource(Integer groupId, MultipartFile file, Integer taskId, String account, String description) {
        //只能关联子任务
        //if(taskMapper.selectTaskById(taskId).getParentId()==null)
        //    return ApiResponse.error("只能关联子任务");

        //存储文件夹
        String baseDir="resources/"+infoGetMapper.selectGroup(groupId).getGroupResourceLibrary();
        //若不存在则创建文件夹
        File folder = new File(baseDir);
        // 检查文件夹是否存在
        if (!folder.exists()) {
            // 创建单个目录
            boolean isCreated = folder.mkdir();
            if (isCreated) {
                System.out.println("Folder created successfully: " + folder.getAbsolutePath());
            } else {
                System.out.println("Failed to create folder.");
            }
        } else {
            System.out.println("Folder already exists: " + folder.getAbsolutePath());
        }

        String name=file.getOriginalFilename();
        //后缀，若无后缀则为-1
        String prefix = name.lastIndexOf(".") != -1 ? name.substring(name.lastIndexOf(".")) : "-1";
        //生成file_type
        int fileType;
        if(prefix.equals(".pdf"))
            fileType=1;
        else if(prefix.equals(".xlsx")||prefix.equals(".xls"))
            fileType=2;
        else if(prefix.equals(".jpg"))
            fileType=3;
        else if(prefix.equals(".png"))
            fileType=4;
        else if(prefix.equals(".ppt")||prefix.equals(".pptx"))
            fileType=5;
        else if(prefix.equals(".doc")||prefix.equals(".docx"))
            fileType=6;
        else if(prefix.equals(".txt"))
            fileType=7;
        else
            fileType=0;

        //生成留言(除resource_id)
        Message message=new Message();
        message.setTaskId(taskId);
        message.setPostAccount(account);
        message.setMessageContent(description);
        message.setMessageType(0);
        message.setPostedTime(LocalDateTime.now());
        message.setTitle("已上传 "+name);

        //生成随机名字 保证不重复
        String path;
        while(true) {
            path = UUID.randomUUID().toString().replace("-", "") + prefix;
            if(resourceMapper.selectResourceByUrl(path)==null)
                break;
        }

        //上传
        try {
            File filePath=new File(baseDir,path);
            //保存文件至文件夹
            file.transferTo(filePath.getAbsoluteFile());

            Resource temp=new Resource();
            //设置小组id
            temp.setGroupId(groupId);
            //设置文件类型
            temp.setFileType(fileType);
            //设置url
            temp.setUrl(path);
            //设置原始名称
            temp.setOriginName(name);
            //设置上传者account
            temp.setUploader(account);
            //设置上传时间
            temp.setUploadTime(LocalDateTime.now());
            //上传Resource
            resourceMapper.uploadFile(temp);
            //设置留言关联的resource_id
            int resourceId=resourceMapper.selectResourceByUrl(path).getResourceId();
            message.setResourceId(resourceId);
            //上传Message
            messageMapper.uploadMessage(message);
            //上传linker
            ResourceLinker resourceLinker=new ResourceLinker();
            resourceLinker.setResourceId(resourceId);
            resourceLinker.setTaskId(taskId);
            resourceMapper.uploadLinker(resourceLinker);
            return ApiResponse.success(String.valueOf(resourceId));
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败");
        }
    }

    @Override
    public ApiResponse<String> deleteResource(int resourceId) {
        //获取相关数据用于删除
        Resource resource=infoGetMapper.selectResourceById(resourceId);
        String baseDir="resources/"+infoGetMapper.selectGroup(resource.getGroupId()).getGroupResourceLibrary();
        String existedPath=resource.getUrl();
        //删除关联了该文件的留言
        List<Message> messageList=infoGetMapper.getMessageByResourceId(resourceId,0);
        messageList.addAll(infoGetMapper.getMessageByResourceId(resourceId,1));
        for(int m=0;m<messageList.size();m++)
        {
            messageMapper.deleteMessage(messageList.get(m).getMessageId());
        }
        //删除resource_linker
        resourceMapper.deleteLinker(resourceId);
        //删除resource
        resourceMapper.deleteResource(resourceId);
        //实际删除文件
        if(existedPath!=null){
            File deletePath=new File(baseDir,existedPath);
            if(deletePath.delete()){
                System.out.println("删除成功");
            }
            else {
                System.out.println("删除失败");
            }
        }
        return ApiResponse.success("success");
    }

    @Override
    public void downloadResource(HttpServletResponse response, int resourceId) throws Exception {

        Resource resource=infoGetMapper.selectResourceById(resourceId);
        String baseDir="resources/"+infoGetMapper.selectGroup(resource.getGroupId()).getGroupResourceLibrary();
        String existedPath=resource.getUrl();

        File file = new File(baseDir,existedPath);
        if (!file.exists()) {
            throw new Exception("当前下载的文件不存在，请检查路径是否正确");
        }

        // 清空 response
        response.reset();
        response.setCharacterEncoding("UTF-8");

        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        response.setContentType("application/octet-stream");

        // 将文件读到输入流中
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while((len = is.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
