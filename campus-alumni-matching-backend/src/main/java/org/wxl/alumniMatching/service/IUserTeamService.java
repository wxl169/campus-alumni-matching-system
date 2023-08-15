package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
