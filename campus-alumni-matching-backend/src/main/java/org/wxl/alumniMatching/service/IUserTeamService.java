package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  用户队伍关系服务类
 * </p>
 * @author 16956
 */
public interface IUserTeamService extends IService<UserTeam> {

    /**
     * 获取某队伍当前人数
     *
     * @param teamId 队伍主键
     * @return
     */
    long countTeamUserByTeamId(long teamId);

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
    Map<String, Integer> getUserTags(Long teamId);
}
