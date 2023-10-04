package org.wxl.alumniMatching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
 * @since 2023-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_message_user")
@ApiModel(value="MessageUser对象", description="")
public class MessageUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "消息接收状态（0-未接收，1-接收）")
    private Integer status;

    @ApiModelProperty(value = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送用户id")
    private Long sendUserId;

    @ApiModelProperty(value = "接收用户id")
    private Long receiveUserId;

    @ApiModelProperty(value = "展示对象(0-两位都展示，id-是谁的id就展示谁)")
    private Integer messageShow;

    @ApiModelProperty(value = "是否为系统消息 0-不是，1-是")
    private Integer isSystem;

    @ApiModelProperty(value = "是否删除（0-未删除，1-删除）")
    private Integer isDelete;


}
