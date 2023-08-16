package org.wxl.alumniMatching.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.wxl.alumniMatching.domain.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 16956
 */
@Data
public class TeamUserListVo implements Serializable {

    private static final long serialVersionUID = -7043676871658194013L;
    @ApiModelProperty(value = "队伍主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    private String teamName;
    @ApiModelProperty(value = "群头像")
    private String avatarUrl;
    @ApiModelProperty(value = "队伍描述")
    private String description;

    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "队长id")
    private Long leaderId;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "加入队伍的成员")
    List<UserTagVO> userList;
}
