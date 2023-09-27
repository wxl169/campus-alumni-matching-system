package org.wxl.alumniMatching.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class MessageTeamLogVO {

    private Long id;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送用户id")
    private Long sendUserId;

    @ApiModelProperty(value = "接收群id")
    private Long teamId;
    @ApiModelProperty(value = "消息是否已读（0-未读，1-已读）")
    private Integer status;
}
