package org.wxl.alumniMatching.contant;

/**
 * 队伍常量
 * @author 16956
 */
public interface TeamConstant {

    //  --------------------- 组队人数限制 ----------------------------
    /**
     * 队伍最少参加人数
     */
    int TEAM_MIN_NUM = 1;

    /**
     * 队伍最大参加人数
     */
    int TEAM_MAX_NUM = 10;

    //  --------------------- 标题字数限制 ----------------------------

    /**
     * 队伍标题最少2字
     */
     int TEAM_NAME_MIN = 2;

    /**
     * 队伍标题最大15字
     */
    int TEAM_NAME_MAX = 15;

    //  --------------------- 描述字数限制 ----------------------------

    /**
     * 队伍描述最大200字
     */
    int TEAM_DESCRIPTION_MAX = 200;

    //  --------------------- 队伍密码限制 ----------------------------

    /**
     * 队伍密码固定6位
     */
    int TEAM_PASSWORD_NUM = 6;
    /**
     * 密码存在空格
     */
    String TEAM_PASSWORD_EXIT_SPACE = " ";

    //  --------------------- 创建队伍限制 ----------------------------

    /**
     * 每位用户最多创建5个队伍
     */
    int TEAM_CREATE_MAX_NUM = 5;

    //  --------------------- 过期时间 ----------------------------
    /**
     * 当天过期
     */
    int TEAM_MIN_EXPIRED_TIME = 0;
    /**
     * 队伍过期预提醒时间
     */
    int TEAM_MAX_EXPIRED_TIME = 7;
}
