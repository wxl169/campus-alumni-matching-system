package org.wxl.alumniMatching.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private Long id;
    private String username;
    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 学校
     */
    private String school;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 状态 0——正常
     */
    private Integer userStatus;

    /**
     * 用户角色 0——普通用户，1——管理员
     */
    private Integer userRole;

    /**
     * 标签列表
     */
    private String tags;

}
