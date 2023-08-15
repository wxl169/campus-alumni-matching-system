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
     * 账号存在空格
     */
    String USER_ACCOUNT_EXIT_SPACE = " ";

}
