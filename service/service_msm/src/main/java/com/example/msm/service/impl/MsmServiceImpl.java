package com.example.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.example.msm.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    // 发送短信的方法
    @Override
    public boolean send(Map<String, Object> param, String phone) {
        if(StringUtils.isEmpty(phone)) return false;
        DefaultProfile profile =
                DefaultProfile.getProfile("default", "LTAI5tEEJmuaJ45oMVSNBPT6", "sPx5p04J6st18wUeMzRXtMp2MLmT8a");
        IAcsClient client = new DefaultAcsClient(profile);

        // 设置相关参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        // 设置发送相关参数
        request.putQueryParameter("PhoneNumbers", phone); // 手机号
        request.putQueryParameter("SignName", "才华在线网课平台"); // 申请阿里云的签名名称
        request.putQueryParameter("TemplateCode",  "SMS_461405862"); // 申请阿里云的模版名称
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param)); // 验证码数据，转换成Json数据传递

        try {
            // 最终发送
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
