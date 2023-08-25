package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  用户队伍关系Mapper 接口
 * </p>
 * @author 16956
 */
public interface UserTeamMapper extends BaseMapper<UserTeam> {
    /**
     * 获取id加入的队伍的id集合
     *
     * @param userId 当前用户的id
     * @return 返回已加入队伍的id集合
     */
    List<Long> getJoinTeamId(Long userId);
}
