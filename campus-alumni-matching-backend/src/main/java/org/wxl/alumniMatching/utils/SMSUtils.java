package org.wxl.alumniMatching.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.wxl.alumniMatching.config.AliyunConfig;

import javax.annotation.Resource;

/**
 * @author 16956
 */
@Component
public class SMSUtils {
    @Resource
    public AliyunConfig aliyunConfig;
 
    public void sendSMS(String phone,String code){
        DefaultProfile profile = DefaultProfile.getProfile("cn-chengdu",
                aliyunConfig.getAccessKeyId(),
                aliyunConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setSignName("校园学院匹配系统短信验证");
        request.setTemplateCode("SMS_277415475");
        request.setPhoneNumbers(phone);
        request.setTemplateParam("{\"code\": \"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}