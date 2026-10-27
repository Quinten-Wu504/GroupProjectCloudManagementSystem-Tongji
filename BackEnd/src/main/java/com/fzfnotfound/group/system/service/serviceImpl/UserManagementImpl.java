package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.User;
import com.fzfnotfound.group.system.mapper.UserMapper;
import com.fzfnotfound.group.system.service.UserManagement;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UserManagementImpl implements UserManagement {

    @Resource
    private UserMapper usermapper;

    @Override
    public ApiResponse<String> nameChange(User user) {
        if(user.getNickname().isEmpty())
            return ApiResponse.error("新名字不能为空");

        usermapper.changeName(user);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> passwordChange(User user) {
        String pwd=user.getPassword();
        //不少于8位
        if(pwd.length()<8)
            return ApiResponse.error("密码长度不得小于8位");
        //不包含空格
        if(pwd.contains(" "))
            return ApiResponse.error("密码不得包含空格");
        //字符类型不少于3种
        int count=0;
        if(pwd.matches(".*[a-z].*"))
            count++;
        if(pwd.matches(".*[A-Z].*"))
            count++;
        if(pwd.matches(".*[0-9].*"))
            count++;
        if(pwd.contains("!")||pwd.contains("@")||
                pwd.contains("$")||pwd.contains("%")||
                pwd.contains("#")||pwd.contains("^")||
                pwd.contains("&")||pwd.contains("*")||
                pwd.contains("(")||pwd.contains(")")||
                pwd.contains("_")||pwd.contains("+")||
                pwd.contains("-")||pwd.contains("=")||
                pwd.contains("{")||pwd.contains("}")||
                pwd.contains("[")||pwd.contains("]")||
                pwd.contains(":")||pwd.contains(";")||
                pwd.contains("\"")||pwd.contains("'")||
                pwd.contains("<")||pwd.contains(">")||
                pwd.contains(",")||pwd.contains(".")||
                pwd.contains("?")||pwd.contains("/")||
                pwd.contains("|")||pwd.contains("\\")||
                pwd.contains("~")||pwd.contains("`"))
            count++;
        if(count<3)
            return ApiResponse.error("字符类型不得少于三种");

        usermapper.changePassword(user);
        return ApiResponse.success("success");
    }

    @Override
    public ApiResponse<String> deleteAccount(String account) {
        if(usermapper.deleteAccount(account)!=0){
            return ApiResponse.success("success");
        }
        return ApiResponse.error("failed");
    }

    @Override
    public ApiResponse<String> updateUserHeadPortrait(String account, MultipartFile file) {
        //存储文件夹
        String baseDir="images/users";
        String name=file.getOriginalFilename();
        String prefix = name.lastIndexOf(".") != -1 ? name.substring(name.lastIndexOf(".")) : ".jpg";
        String path = UUID.randomUUID().toString().replace("-", "") + prefix;
        try {
            File filePath=new File(baseDir,path);
            //如果用户已经有头像，删除原来的头像
            String existedPath=usermapper.selectHeadPortraitByAccount(account);
            if(existedPath!=null&&!existedPath.equals("defaultUser.jpg")){
                File deletePath=new File(baseDir,existedPath);
                if(deletePath.delete()){
                    System.out.println("删除成功");
                }
                else {
                    System.out.println("删除失败");
                }
            }
            //保存头像至文件夹
            file.transferTo(filePath.getAbsoluteFile());
            //保存url
            usermapper.updateUserHeadPortrait(account,path);
            return ApiResponse.success("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败");
        }
    }
}
