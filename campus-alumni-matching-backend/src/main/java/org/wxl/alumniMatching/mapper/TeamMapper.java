package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.UserListVO;
import org.wxl.alumniMatching.domain.vo.UserShowVO;
import org.wxl.alumniMatching.domain.vo.UserTagVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  队伍Mapper 接口
 * </p>
 * @author 16956
 */
public interface TeamMapper extends BaseMapper<Team> {

    /**
     *返回参加队伍的成员的信息
     *
     * @param teamUserListId 队伍的主键
     * @return 用户信息
     */
    List<UserShowVO> getUserList(Long teamUserListId);

    /**
     * 获取当前用户加入的队伍信息
     *
     * @param userId 当前用户的id
     * @return  获取当前用户加入的队伍信息
     */
    List<Team> getUserJoinTeamList(Long userId);

    /**
     * 获取队伍信息
     * 只查找队伍的主键，最大队伍人数要大于2，队伍要没有满员，队伍状态不能是私密的，队伍没有过期，且当前用户没有加入的队伍
     *
     * @param  userId 用户id
     * @return 队伍主键
     */
    List<Long> selectMatchTeams(Long userId);

    /**
     * 查询所有设置了过期时间的队伍
     *
     * @return 队伍列表
     */
    List<Team> selectHavingExpireTime();


    /**
     * 获取当前登录用户加入队伍的id集合
     *
     * @param loginUserId 当前登录用户
     * @return 返回当前登录用户加入队伍id集合
     */
    Set<Long> selectUserJoinTeamId(Long loginUserId);

    /**
     * 查询队伍名和头像
     *
     * @param teamId 队伍id
     * @return 返回队伍名和头像
     */
    Team selectTeamNameAndAvatar(Long teamId);

}
