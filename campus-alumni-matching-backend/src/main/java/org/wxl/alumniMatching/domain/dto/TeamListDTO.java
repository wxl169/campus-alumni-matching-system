package org.wxl.alumniMatching.domain.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 16956
 */

@Data
public class TeamListDTO  implements Serializable {
    private static final long serialVersionUID = 4559306890419270938L;
    @ApiModelProperty(value = "id列表")
    private List<Long> idList;

    @ApiModelProperty(value = "搜索关键词（同时对队伍名称和描述搜索）")
    private String searchText;

    @ApiModelProperty(value = "队伍主键")
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    private String teamName;

    @ApiModelProperty(value = "队伍描述")
    private String description;

    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    @ApiModelProperty(value = "队长id")
    private Long leaderId;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    private Integer status;

}