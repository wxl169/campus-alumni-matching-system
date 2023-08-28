package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 16956
 */
@Data
public class UserAddTagDTO implements Serializable {
    private static final long serialVersionUID = -4321127387425561595L;

    private List<String> tagNameList;
}
