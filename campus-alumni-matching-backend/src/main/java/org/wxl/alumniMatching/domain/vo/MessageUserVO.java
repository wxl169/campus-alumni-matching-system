package org.wxl.alumniMatching.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class MessageUserVO implements Serializable {
    private static final long serialVersionUID = -8397829861890686545L;
    @ApiModelProperty(value = "消息主键")
    private Long id;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送用户id")
    private Long sendUserId;

    @ApiModelProperty(value = "发送用户名")
    private String sendUserName;

    @ApiModelProperty(value = "接收用户id")
    private Long receiveUserId;
    @ApiModelProperty(value = "接收用户名")
    private String receiveUserName;
}
