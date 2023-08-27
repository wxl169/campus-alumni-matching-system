package org.wxl.alumniMatching.contant;

/**
 * 用户常量
 * @author 16956
 */

public interface UserConstant {

    /**
     * 用户登录状态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    //  --------------------- 权限 ----------------------------

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    //  -------------------------用户状态 ---------------------

    /**
     * 用户状态正常
     */
    int USER_STATUS_NORMAL = 0;

    //  -------------------------用户账户密码设置条件 ------------

    /**
     * 用户账号最小长度
     */
    int USER_ACCOUNT_MIN_LENGTH = 5;
    /**
     * 用户账号最大长度
     */
    int USER_ACCOUNT_MAX_LENGTH = 10;

    /**
     * 用户密码最小长度
     */
    int USER_PASSWORD_MIN_LENGTH = 8;
    /**
     * 用户密码最大长度
     */
    int USER_PASSWORD_MAX_LENGTH = 15;

    /**
     * 用户名最小长度
     */
    int USER_NAME_MIN_LENGTH = 1;
    /**
     * 用户密码最大长度
     */
    int USER_NAME_MAX_LENGTH = 10;

    /**
     * 账号存在空格
     */
    String USER_ACCOUNT_EXIT_SPACE = " ";

    /**
     * 个人简介最大长度
     */
    int USER_PROFILE_MAX_LENGTH = 50;


    /**
     * 用户默认头像
     */
    String USER_DEFAULT_AVATAR = "https://avatars.githubusercontent.com/u/110905530?s=400&u=93c77349f78d6dc20c3ca8c4e6e758c0d7767108&v=4";

    /**
     * 判断账号是否含特殊符号的正则表达式
     */
    String USER_ACCOUNT_REGULAR = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
}
