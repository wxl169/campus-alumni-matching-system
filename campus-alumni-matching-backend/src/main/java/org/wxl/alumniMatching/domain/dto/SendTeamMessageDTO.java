package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class SendTeamMessageDTO implements Serializable {
    private static final long serialVersionUID = 4599122562934361934L;

    /**
     * 发送消息的群聊
     */
    private Long teamId;
    /**
     * 发送的内容
     */
    private String content;
    /**
     * 页面摆放位置
     */
    private String position;

}
