package com.fzfnotfound.group.system.controller;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.DownloadInfo;
import com.fzfnotfound.group.system.entity.Resource;
import com.fzfnotfound.group.system.entity.ResourceControllerEntity;
import com.fzfnotfound.group.system.mapper.InfoGetMapper;
import com.fzfnotfound.group.system.service.ResourceService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;

@CrossOrigin
@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InfoGetMapper infoGetMapper;

    @PostMapping("/uploadResource")
    public ApiResponse<String> uploadResource(@RequestParam("groupId")int groupId,@RequestParam("file")MultipartFile file,@RequestParam("taskId")int taskId,@RequestParam("account")String account,@RequestParam("description")String description){
        return resourceService.uploadResource(groupId,file,taskId,account,description);
    }

    @PostMapping("/deleteResource")
    public ApiResponse<String> deleteResource(@RequestBody ResourceControllerEntity resourceController){
        return resourceService.deleteResource(resourceController.getResourceId());
    }

    @PostMapping("/downloadResource")
    public void downloadResource(HttpServletResponse response, @RequestParam("resourceId")int resourceId) throws Exception {

        resourceService.downloadResource(response,resourceId);
    }

    @PostMapping("/downloadResourceNew")
    public ResponseEntity<ByteArrayResource> downloadResourceNew(@RequestBody DownloadInfo downloadInfo) throws Exception {
        if(downloadInfo==null){
            ApiResponse.error("传入参数为空");
        }
        int resourceId =downloadInfo.getResourceId();

        Resource resourceT=infoGetMapper.selectResourceById(resourceId);
        if(resourceT==null){
            ApiResponse.error("未找到资源");
        }
        String baseDir=infoGetMapper.selectGroup(resourceT.getGroupId()).getGroupResourceLibrary();
        String existedPath=resourceT.getUrl();

        // String targetPath="/"+ Paths.get(baseDir,existedPath).toString();
        String targetPath=Paths.get(baseDir,existedPath).toAbsolutePath().toString();
        System.out.println(targetPath);
        String resourcePath = Paths.get("resources", baseDir,existedPath).toAbsolutePath().toString();
        System.out.println(resourcePath);
        try (InputStream inputStream=new FileInputStream(resourcePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                return null;
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(resourceT.getOriginName(), "UTF-8")
                    .replaceAll("\\+", "%20");
            headers.add("Content-Disposition",
                    "attachment; filename=\"" + new String(resourceT.getOriginName().getBytes("UTF-8"), "ISO-8859-1") +
                            "\"; filename*=UTF-8''" + encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return null;
        }
    }


}
