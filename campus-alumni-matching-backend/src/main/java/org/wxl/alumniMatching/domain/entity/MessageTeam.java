package org.wxl.alumniMatching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 16956
 * @since 2023-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_message_team")
@ApiModel(value="MessageTeam对象", description="")
public class MessageTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "群聊消息主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送用户id")
    private Long sendUserId;

    @ApiModelProperty(value = "接收群id")
    private Long teamId;

    @ApiModelProperty(value = "是否删除（0-未删除，1-删除）")
    private Integer isDelete;


}
