package org.wxl.alumniMatching.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class TeamJoinDTO implements Serializable {
    private static final long serialVersionUID = 4651162625742383785L;
    @ApiModelProperty("队伍id")
    private Long teamId;
    @ApiModelProperty("队伍密码")
    private String password;
}
