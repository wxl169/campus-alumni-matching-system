package org.wxl.alumniMatching.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class UserDetailVO implements Serializable {

    private static final long serialVersionUID = 6005432659408311002L;
    @ApiModelProperty(value = "用户主键")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "个人简介")
    private String profile;

    @ApiModelProperty(value = "标签列表")
    private String tags;

    @ApiModelProperty(value = "是否为登录用户的好友 1表示是好友")
    private Integer isFriend;
}
