package org.wxl.alumniMatching.domain.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * @author 16956
 */
@Data
public class MessageTeamVO implements Serializable {
    private static final long serialVersionUID = -8462116299615789765L;

    private Long sendUserId;
    private String sendUserName;
    private Object content;
    private String sendUserAvatar;
    private Boolean current;

}
