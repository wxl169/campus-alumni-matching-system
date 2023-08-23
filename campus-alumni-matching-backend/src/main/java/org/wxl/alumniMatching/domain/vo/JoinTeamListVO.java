package org.wxl.alumniMatching.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author 16956
 */
@Data
public class JoinTeamListVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "队伍主键")
    private Long id;
    @ApiModelProperty(value = "队伍名称")
    private String teamName;
    @ApiModelProperty(value = "群头像")
    private String avatarUrl;
}
