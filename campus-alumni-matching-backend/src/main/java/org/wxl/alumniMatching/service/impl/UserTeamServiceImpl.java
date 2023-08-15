package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.wxl.alumniMatching.domain.entity.UserTeam;
import org.wxl.alumniMatching.mapper.UserTeamMapper;
import org.wxl.alumniMatching.service.IUserTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  用户队伍关系服务实现类
 * </p>
 * @author 16956
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements IUserTeamService {


    /**
     * 获取某队伍当前人数
     *
     * @param teamId 队伍主键
     * @return
     */
    @Override
    public long countTeamUserByTeamId(long teamId) {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId, teamId);
        return this.count(queryWrapper);
    }
}
