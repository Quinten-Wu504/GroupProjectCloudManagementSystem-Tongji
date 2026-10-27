package com.fzfnotfound.group.system.service.serviceImpl;

import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.Email;
import com.fzfnotfound.group.system.mapper.UserMapper;
import com.fzfnotfound.group.system.service.MailService;
import com.fzfnotfound.group.system.utility.CodeUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CachePut;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Component
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    CodeUtil codeUtil;

    @Override
    @CachePut(key = "#email",value = "emailCode")
    public String sendTemplateMail(String email) throws MessagingException, UnsupportedEncodingException {

        //判断邮箱格式
        // 正则表达式：匹配以 @ 和 .edu.cn 结尾的邮箱
        String regex = "^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9.-]+)\\.edu\\.cn$";
        // 使用 Pattern 和 Matcher 类进行匹配
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
        {
            return "not school email";
        }
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        // 创建 MimeMessageHelper
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        // 发件人邮箱和名称
        //上述不需要改动
        helper.setFrom(javaMailSender.getUsername(), "springdoc");
        // 收件人邮箱
        helper.setTo(email);
        // 邮件标题
        helper.setSubject("校园验证");
        String code= codeUtil.generate(email);
        // 邮件正文，第二个参数表示是否是HTML正文
        helper.setText(code, true);
        // 发送
        javaMailSender.send(message);

        return code;
    }

    @Override
    public ApiResponse<String> checkCode(Email emailEntity) {
        String code=emailEntity.getCode();
        System.out.println("Email:"+emailEntity.getEmail());
        String cacheCode=codeUtil.getEmailCacheCode(emailEntity.getEmail());
        System.out.println("cacheCode:"+cacheCode);
        System.out.println("code:"+code);
        if(code.equals("not school email"))
            return ApiResponse.error("非学校邮箱");
        //是否通过验证
        Boolean isPass = code.equals(cacheCode);
        System.out.println(isPass);

        //如果验证成功，返回认证学校，否则说明验证失败
        if(isPass) {
            String email = emailEntity.getEmail(); // 示例邮箱地址
            // 正则表达式：匹配以 @ 和 .edu.cn 结尾的邮箱
            String regex = "^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9.-]+)\\.edu\\.cn$";
            // 使用 Pattern 和 Matcher 类进行匹配
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            //提取出学校域名部分
            String domainPart="NULL";
            if(matcher.matches());
            {
                domainPart = matcher.group(1); // 第一个捕获组（即 @ 后的部分）
            }
            String school="NOT KNOWN";
            //学校判断部分
            if(domainPart.equals("tongji"))
                school="同济大学";
            else if(domainPart.equals("m.fudan"))
                school="复旦大学";
            else if(domainPart.equals("stu.ecnu"))
                school="华东师范大学";

            //修改用户的school和email字段
            userMapper.updateUserSchool(school,emailEntity.getAccount());
            userMapper.updateUserEmail(email,emailEntity.getAccount());
            return ApiResponse.success(school);
        }
        else
            return ApiResponse.error("验证码错误");

    }
}