package org.wxl.alumniMatching.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class MessageVO implements Serializable {
    private static final long serialVersionUID = -840940367971075108L;
    /**
     * 判断是不是系统消息
     */
    private Integer isSystem;
    /**
     * 若为系统消息则为null,不然则为发送给的用户用户名
     */
    private Long sendUserId;
    private Long receiveUserId;
    private Object content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendTime;
}
