package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.dto.TeamAddDTO;
import org.wxl.alumniMatching.domain.dto.TeamJoinDTO;
import org.wxl.alumniMatching.domain.dto.TeamListDTO;
import org.wxl.alumniMatching.domain.dto.TeamUpdateDTO;
import org.wxl.alumniMatching.domain.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.JoinTeamListVO;
import org.wxl.alumniMatching.domain.vo.PageVO;
import org.wxl.alumniMatching.domain.vo.TeamUserListVO;

import java.util.List;

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

    /**
     * 分页获取队伍信息
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param teamListDTO 查询条件
     * @param loginUser 当前登录用户
     * @return 获取的分页队伍数据
     */
    PageVO teamListPage(Integer pageNum, Integer pageSize, TeamListDTO teamListDTO, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamListDTO 加入队伍的主键和密码
     * @param loginUser 当前登录用户
     * @return 判断是否加入成功
     */
    boolean joinTeam(TeamJoinDTO teamListDTO, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamId 队伍主键
     * @param loginUser 当前登录用户
     * @return 判断是否退出成功
     */
    boolean quitTeam(Long teamId, User loginUser);

    /**
     * 分页获取队伍信息、但不包括已加入队伍的信息
     *
     * @param teamListDTO 查询条件
     * @param loginUser 当前登录信息
     * @return 获取的分页队伍数据
     */
    PageVO teamList(TeamListDTO teamListDTO, User loginUser);


    /**
     * 查询当前用户已加入的队伍
     *
     * @param loginUser 获取当前登录用户
     * @return 返回已加入队伍列表
     */
    List<JoinTeamListVO> userJoinTeamList(User loginUser);

    /**
     * 获取队伍信息及成员信息
     *
     * @param teamId 队伍id
     * @return 返回队伍及成员信息
     */
    TeamUserListVO getTeamAndUser(Long teamId);

    /**
     * 获取最匹配的队伍信息
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param loginUser 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    PageVO getMatchTeams(Integer pageNum, Integer pageSize, User loginUser);
}
