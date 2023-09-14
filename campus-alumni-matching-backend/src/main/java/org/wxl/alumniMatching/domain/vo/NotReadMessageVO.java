package org.wxl.alumniMatching.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class NotReadMessageVO implements Serializable {
    private static final long serialVersionUID = 3019567757066004208L;
 @ApiModelProperty("消息主键id")
 private Long id;
   @ApiModelProperty(value = "发送人id")
    private Long sendUserId;
    @ApiModelProperty(value = "发送人姓名")
    private String sendUserName;
    @ApiModelProperty(value = "发送人头像")
    private String sendUserAvatar;

    @ApiModelProperty(value = "接收人id")
    private Long receiveUserId;
    @ApiModelProperty(value = "接收人姓名")
    private String receiveUserName;
    @ApiModelProperty(value = "接收人头像")
    private String receiveUserAvatar;
    @ApiModelProperty(value = "最近一条发送内容")
    private String content;
    @ApiModelProperty(value = "是否已读")
    private Integer status;
    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;
}
