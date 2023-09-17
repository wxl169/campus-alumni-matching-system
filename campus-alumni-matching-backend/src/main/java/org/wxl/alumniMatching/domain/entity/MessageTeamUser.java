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
 * @since 2023-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_message_team_user")
@ApiModel(value="MessageTeamUser对象", description="")
public class MessageTeamUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "群消息关联表")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "接收用户id")
    private Long receiveUserId;

    @ApiModelProperty(value = "群消息id")
    private Long messageTeamId;

    @ApiModelProperty(value = "接收状态(0-未接收，1-接收）")
    private Integer status;

    @ApiModelProperty(value = "接收时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "是否删除（0-未删除，1-删除）")
    private Integer isDelete;


}
