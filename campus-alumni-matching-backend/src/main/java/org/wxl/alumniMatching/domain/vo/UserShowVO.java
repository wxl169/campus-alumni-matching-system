package org.wxl.alumniMatching.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author 16956
 */
@Data
public class UserShowVO {

    @ApiModelProperty(value = "用户主键")
    private Long id;
    @ApiModelProperty(value = "用户昵称")
    private String username;
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;
}
