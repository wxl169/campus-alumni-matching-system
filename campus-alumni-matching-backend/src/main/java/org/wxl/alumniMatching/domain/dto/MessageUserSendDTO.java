package org.wxl.alumniMatching.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class MessageUserSendDTO implements Serializable {
    private static final long serialVersionUID = -600868767023488355L;
    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "接收用户id")
    private Long receiveUserId;
}
