package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class HistoryTeamMessageDTO implements Serializable {
    private static final long serialVersionUID = 5673975768117952171L;
    private String content;
    private Long teamId;
    private String sendTime;
}
