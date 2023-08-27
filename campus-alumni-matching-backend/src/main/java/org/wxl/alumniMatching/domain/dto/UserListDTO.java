package org.wxl.alumniMatching.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO  implements Serializable {
    private static final long serialVersionUID = -1723774660638238548L;
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 性别
     */
    private Integer gender;

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
