package org.wxl.alumniMatching.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTagVO {
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
     * 个人介绍
     */
    private String profile;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 标签列表
     */
    private String tags;
}
