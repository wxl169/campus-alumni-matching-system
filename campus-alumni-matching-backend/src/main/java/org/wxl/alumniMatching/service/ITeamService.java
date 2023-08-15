package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.dto.TeamAddDTO;
import org.wxl.alumniMatching.domain.dto.TeamUpdateDTO;
import org.wxl.alumniMatching.domain.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;

/**
 * <p>
 *  队伍服务类
 * </p>
 * @author 16956
 */
public interface ITeamService extends IService<Team> {

    /**
     * 添加组队
     *
     * @param teamAddDTO 队伍信息
     * @param loginUser 登录用户信息
     * @return 主键id
     */
    Long addTeam(TeamAddDTO teamAddDTO, User loginUser);

    /**
     *修改组队信息
     *
     * @param teamUpdateDTO 修改后的信息
     * @param loginUser 登录用户
     * @return 修改是否成功
     */
    boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser);

    /**
     * 删除队伍信息
     *
     * @param teamId 队伍id
     * @param loginUser 当前登录用户
     * @return 判断是否删除成功
     */
    boolean deleteTeam(long teamId, User loginUser);
}
