package org.wxl.alumniMatching.contant;

/**
 * 手机邮箱登格式检查
 * @author 16956
 */
public interface RegexConstant {
    /**
     * 手机号正则
     */
     String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * 邮箱正则
     */
     String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
     String PASSWORD_REGEX = "^\\w{4,32}$";
    /**
     * 验证码正则, 6位数字或字母
     */
     String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";

}
