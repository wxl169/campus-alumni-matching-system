package org.wxl.alumniMatching.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVO implements Serializable {
    private static final long serialVersionUID = 42L;

    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 状态 0——正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户角色 0——普通用户，1——管理员
     */
    private Integer userRole;

    /**
     * 标签列表
     */
    private String tags;
}
