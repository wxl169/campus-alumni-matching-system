package org.wxl.alumniMatching.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 *
 * @author 16956
 */
@Data
public class DeleteDTO implements Serializable {
    private static final long serialVersionUID = -2286615406073833302L;
    private long id;

}
