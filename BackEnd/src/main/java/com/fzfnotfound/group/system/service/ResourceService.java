package com.fzfnotfound.group.system.service;

import com.fzfnotfound.group.system.entity.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    public ApiResponse<String> uploadResource(Integer groupId, MultipartFile file, Integer taskId, String account, String description);

    public ApiResponse<String> deleteResource(int resourceId);

    public void downloadResource(HttpServletResponse response,int resourceId) throws Exception;
}
