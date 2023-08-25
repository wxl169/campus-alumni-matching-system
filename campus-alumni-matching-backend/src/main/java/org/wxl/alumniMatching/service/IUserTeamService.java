package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
}
