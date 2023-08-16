package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.TeamConstant;
import org.wxl.alumniMatching.domain.dto.TeamAddDTO;
import org.wxl.alumniMatching.domain.dto.TeamJoinDTO;
import org.wxl.alumniMatching.domain.dto.TeamListDTO;
import org.wxl.alumniMatching.domain.dto.TeamUpdateDTO;
import org.wxl.alumniMatching.domain.entity.Team;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.entity.UserTeam;
import org.wxl.alumniMatching.domain.enums.TeamStatusEnum;
import org.wxl.alumniMatching.domain.vo.PageVO;
import org.wxl.alumniMatching.domain.vo.TeamUserListVo;
import org.wxl.alumniMatching.domain.vo.UserTagVO;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private RedissonClient redissonClient;

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
     * 查询队伍列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param teamListDTO 查询条件
     * @param loginUser 当前登录用户
     * @return 获取的分页队伍数据
     */
    @Override
    public PageVO teamList(Integer pageNum, Integer pageSize, TeamListDTO teamListDTO, User loginUser) {
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 8;
        }
        Page<Team> page = new Page<>(pageNum,pageSize);

        Long teamId = teamListDTO.getId();
        List<Long> idList = teamListDTO.getIdList();
        String searchText = teamListDTO.getSearchText();
        String teamName = teamListDTO.getTeamName();
        String description = teamListDTO.getDescription();
        Integer maxNum = teamListDTO.getMaxNum();
        Long leaderId = teamListDTO.getLeaderId();

        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                //查询跟选择队伍id一致的
                .eq(teamId!=null,Team::getId,teamId)
                //查询多个队伍id
                .in(CollectionUtils.isNotEmpty(idList),Team::getId,idList)
                //如果关键词不为空，根据队伍名和介绍查询
                .and(StringUtils.isNotBlank(searchText),qw -> qw
                        .like(Team::getTeamName,searchText)
                        .or()
                        .like(Team::getDescription,searchText)
                )
                //根据队伍名模糊查询
                .like(StringUtils.isNotBlank(teamName),Team::getTeamName,teamName)
                //根据队伍介绍模糊查询
                .like(StringUtils.isNotBlank(description),Team::getDescription,description)
                //根据最大人数查询
                .eq(maxNum != null && maxNum > 0,Team::getMaxNum,maxNum)
                //根据创建人id查询
                .eq(leaderId != null,Team::getLeaderId,leaderId);

        Integer status = teamListDTO.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        boolean isAdmin = userService.isAdmin(loginUser);
        //只有管理员才能查看非公开的房间
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }
        queryWrapper.eq(Team::getStatus, statusEnum.getValue());
        //不展示已过期的队伍
        queryWrapper.and(
                qw->qw.gt(Team::getExpireTime,new Date())
                .or()
                .isNull(Team::getExpireTime)
        );
        page(page,queryWrapper);

        //关联查询创建人的用户信息
        List<TeamUserListVo> teamUserListVos = BeanCopyUtils.copyBeanList(page.getRecords(), TeamUserListVo.class);
        teamUserListVos = teamUserListVos.stream().peek(teamUserList -> {
            Long teamUserListId = teamUserList.getId();
            List<UserTagVO> userList = teamMapper.getUserList(teamUserListId);
            teamUserList.setUserList(userList);
        }).collect(Collectors.toList());
        return new PageVO(teamUserListVos,page.getTotal());
    }

    /**
     * 加入队伍
     * @param teamListDTO 加入队伍的主键和密码
     * @param loginUser 当前登录用户
     * @return 判断是否加入成功
     */
    @Override
    public boolean joinTeam(TeamJoinDTO teamListDTO, User loginUser) {
        if (teamListDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        Long teamId = teamListDTO.getTeamId();
        Team team = this.getTeamById(teamId);
        LocalDateTime expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamListDTO.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getId();
        // 只有一个线程能获取到锁
        RLock lock = redissonClient.getLock("alumniMatching:team:joinTeam:lock");
        try {
            // 抢到锁并执行
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    System.out.println("getLock: " + Thread.currentThread().getId());
                    LambdaQueryWrapper<UserTeam> userTeamQueryWrapper = new LambdaQueryWrapper<>();
                    userTeamQueryWrapper.eq(UserTeam::getUserId, userId);
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinNum >= 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
                    }
                    // 不能重复加入已加入的队伍
                    userTeamQueryWrapper = new LambdaQueryWrapper<>();
                    userTeamQueryWrapper.eq(UserTeam::getUserId, userId);
                    userTeamQueryWrapper.eq(UserTeam::getTeamId, teamId);
                    long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                    }
                    // 已加入队伍的人数
                    long teamHasJoinNum = userTeamService.countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    // 修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(LocalDateTime.now());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * 退出队伍
     *
     * @param teamId 队伍主键
     * @param loginUser 当前登录用户
     * @return 判断是否退出成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(Long teamId, User loginUser) {
        if (teamId == null || teamId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();

        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);

        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>(queryUserTeam);
        //判断当前用户是否加入该队伍
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinNum = userTeamService.countTeamUserByTeamId(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            // 删除队伍
            this.removeById(teamId);
        } else {
            // 队伍还剩至少两人
            // 是队长
            if (team.getLeaderId() == userId) {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                LambdaQueryWrapper<UserTeam> userTeamQueryWrapper = new LambdaQueryWrapper<>();
                userTeamQueryWrapper.eq(UserTeam::getTeamId, teamId);
                //查找最早入队的两人
                userTeamQueryWrapper.last("order by id asc limit 2");

                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统内部异常");
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setLeaderId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.remove(queryWrapper);
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
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "需超时时间 > 当前时间");
            }
        }

    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId 队伍主键
     * @return 某队伍详细信息
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

}
