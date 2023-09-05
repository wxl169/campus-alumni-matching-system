package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 16956
 */
@Data
public class UserMessageCodeDTO implements Serializable {
    private static final long serialVersionUID = -852701261463961255L;
    private String phone;
    private String messageCode;
}
