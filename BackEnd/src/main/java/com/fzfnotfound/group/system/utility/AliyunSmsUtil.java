package com.fzfnotfound.group.system.utility;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;





public class AliyunSmsUtil {

    static final String accessKeyId = System.getenv("ALIYUN_ACCESS_KEY_ID");
    static final String accessKeySecret = System.getenv("ALIYUN_ACCESS_KEY_SECRET");
    static final String signName = System.getenv("ALIYUN_SMS_SIGN_NAME");
    static final String templateCode = System.getenv("ALIYUN_SMS_TEMPLATE_CODE");
    static final String domain = "dysmsapi.aliyuncs.com";
    static final String serverLocation = "cn-hangzhou";
    static final String product = "Dysmsapi";

    public AliyunSmsUtil() {
    }

//    public static void main(String[] args) throws Exception {
//    }

    public static SendSmsResponse sendSmsByParams(String mobile, String code) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile(serverLocation, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(mobile);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        String jsonParams = "{\"code\":\"" + code + "\"}";
        request.setTemplateParam(jsonParams);
        request.setOutId("yourOutId");
        SendSmsResponse sendSmsResponse = (SendSmsResponse)acsClient.getAcsResponse(request);
        System.out.println((new Gson()).toJson(sendSmsResponse));
        return sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK") ? sendSmsResponse : sendSmsResponse;
    }
}