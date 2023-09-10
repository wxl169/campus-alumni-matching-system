package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class SendMessageDTO implements Serializable {
    private static final long serialVersionUID = 1315989340950041205L;

    /**
     * 接收人id
     */
    private Long toUserId;
    /**
     * 消息
     */
    private String content;

}
