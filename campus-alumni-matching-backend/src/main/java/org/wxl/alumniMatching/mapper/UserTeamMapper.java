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

    /**
     * 将参与该队伍的成员的标签信息选出出现次数最多的标签
     * @param teamId 队伍id
     * @return 返回去重后的标签信息
     */
    List<String> getUserTags(Long teamId);

    /**
     * 获取加入队伍的成员id
     * @param teamId 队伍id
     * @return 成员id列表
     */
    List<Long> getTeamUserId(Long teamId);
}
