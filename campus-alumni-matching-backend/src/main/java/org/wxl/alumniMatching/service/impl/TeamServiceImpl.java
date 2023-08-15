package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.TeamConstant;
import org.wxl.alumniMatching.domain.dto.TeamAddDTO;
import org.wxl.alumniMatching.domain.dto.TeamUpdateDTO;
import org.wxl.alumniMatching.domain.entity.Team;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.entity.UserTeam;
import org.wxl.alumniMatching.domain.enums.TeamStatusEnum;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.TeamMapper;
import org.wxl.alumniMatching.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.service.IUserService;
import org.wxl.alumniMatching.service.IUserTeamService;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * <p>
 *  队伍服务实现类
 * </p>
 * @author 16956
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {
    @Resource
    private IUserTeamService userTeamService;
    @Resource
    private IUserService userService;


    /**
     * 添加组队
     *
     * @param teamAddDTO 队伍信息
     * @param loginUser 登录用户信息
     * @return 主键id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(TeamAddDTO teamAddDTO, User loginUser) {
        if (teamAddDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"请登录账号");
        }
        //登录用户id
        final long userId = loginUser.getId();
        Team team = BeanCopyUtils.copyBean(teamAddDTO, Team.class);

        //判断队伍信息是否符合要求
        judgmentTeamInformation(team);


        //队伍状态
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamAddDTO.getStatus());
        //如果队伍是公开或者私密的就取消密码
        if (TeamStatusEnum.PUBLIC.equals(statusEnum)){
            teamAddDTO.setPassword("");
        }

        //将每位用户允许参加的队伍个数控制在 1 ~ 5
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getLeaderId,loginUser.getId());
        //已创建的队伍数量
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum >= TeamConstant.TEAM_CREATE_MAX_NUM){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }

        //插入队伍信息到队伍表
        team.setLeaderId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }

        //插入信息到用户队伍关系表中
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(LocalDateTime.now());
        save = userTeamService.save(userTeam);
        if (!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }


    /**
     *修改组队信息
     *
     * @param teamUpdateDTO 修改后的信息
     * @param loginUser 登录用户
     * @return 修改是否成功
     */
    @Override
    public boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser) {
        if (teamUpdateDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Long teamId = teamUpdateDTO.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        //获取原队伍信息
        Team oldTeam = this.getById(teamId);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 只有管理员或者队伍的创建者可以修改
        if (!oldTeam.getLeaderId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"权限不足");
        }

        Team updateTeam = BeanCopyUtils.copyBean(teamUpdateDTO, Team.class);

        //判断队伍信息是否按条件填入
        judgmentTeamInformation(updateTeam);

        //队伍状态
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateDTO.getStatus());
        //如果队伍是公开或者私密的就取消密码
        if (TeamStatusEnum.PUBLIC.equals(statusEnum)){
            updateTeam.setPassword("");
        }
        return this.updateById(updateTeam);
    }

    /**
     * 删除队伍信息
     *
     * @param teamId 队伍id
     * @param loginUser 当前登录用户
     * @return 判断是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        // 校验队伍是否存在
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 校验你是不是队伍的队长或管理员
        if (!team.getLeaderId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        // 移除所有加入队伍的关联信息
        LambdaQueryWrapper<UserTeam> userTeamQueryWrapper = new LambdaQueryWrapper<>();
        userTeamQueryWrapper.eq(UserTeam::getTeamId, teamId);
        boolean deleteUserTeam = userTeamService.remove(userTeamQueryWrapper);
        if (!deleteUserTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        boolean deleteTeam = this.removeById(teamId);
        if (!deleteTeam){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍信息失败");
        }
        return true;
    }


    /**
     * 判断队伍信息是否符合条件
     *
     * @param team 队伍信息
     */
    private void judgmentTeamInformation(Team team){
        //队伍人数 1 ~ 10 人
        //设置获取到的队伍人数信息，如果为null则设置为0
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < TeamConstant.TEAM_MIN_NUM || maxNum > TeamConstant.TEAM_MAX_NUM){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //队伍标题 2 ~ 15 字
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() < TeamConstant.TEAM_NAME_MIN || teamName.length() > TeamConstant.TEAM_NAME_MAX){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //队伍描述 <=200
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > TeamConstant.TEAM_DESCRIPTION_MAX) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //status 是否公开 不传默认位 0
        int teamStatus = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }

        //如果 teamStatus = 2 位加密状态，需要密码 且 密码为 6 位
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.contains(TeamConstant.TEAM_PASSWORD_EXIT_SPACE) || password.length() != TeamConstant.TEAM_PASSWORD_NUM ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请按规定设置密码");
            }
        }
        // 超时时间 > 当前时间
        if (team.getExpireTime() != null){
            LocalDateTime expireTime = team.getExpireTime()
                    .toInstant(ZoneOffset.of("+08:00"))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (LocalDateTime.now().isAfter(expireTime)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
            }
        }

    }

}
