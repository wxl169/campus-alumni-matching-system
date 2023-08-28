package org.wxl.alumniMatching.domain.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 16956
 */
@Data
public class TagShowVO implements Serializable {
    private static final long serialVersionUID = 3103190946750252667L;
    @ApiModelProperty(value = "标签名称")
    private String text;

//    @ApiModelProperty(value = "子标签")
//    private List<String> childrenTagName;

    @ApiModelProperty(value = "子标签")
    private List<TagChildrenVO> children;

}
