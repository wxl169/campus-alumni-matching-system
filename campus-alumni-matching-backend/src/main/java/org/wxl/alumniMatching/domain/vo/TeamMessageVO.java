package org.wxl.alumniMatching.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 16956
 */
@Data
public class TeamMessageVO implements Serializable {
    private static final long serialVersionUID = 5868632286404654651L;
    /**
     * 判断是不是系统消息
     */
    private Integer isSystem;
    /**
     * 若为系统消息则为null,不然则为发送给的用户用户名
     */
    private Long sendUserId;
    private Long teamId;
    private Object content;
    private String position;
    private String sendUserName;
    private String sendUserAvatar;
    private LocalDateTime sendTime;
}
