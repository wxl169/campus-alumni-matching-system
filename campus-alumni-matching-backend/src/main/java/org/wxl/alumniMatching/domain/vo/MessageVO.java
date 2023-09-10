package org.wxl.alumniMatching.domain.vo;

import lombok.Data;

import java.io.Serializable;

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
    private Long receiveUserId;
    private Object content;
}
