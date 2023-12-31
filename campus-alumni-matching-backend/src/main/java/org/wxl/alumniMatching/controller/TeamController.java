package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.dto.*;
import org.wxl.alumniMatching.domain.entity.Team;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.JoinTeamListVO;
import org.wxl.alumniMatching.domain.vo.PageVO;
import org.wxl.alumniMatching.domain.vo.TeamByIdVO;
import org.wxl.alumniMatching.domain.vo.TeamUserListVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.ITeamService;
import org.wxl.alumniMatching.service.IUserService;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author 16956
 */
@Api(tags = "队伍模块")
@RestController
//@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000","http://42.193.15.245:8080"},allowCredentials = "true")
@RequestMapping("/team")
public class TeamController {
    @Resource
    private ITeamService teamService;
    @Resource
    private IUserService userService;


    /**
     * 添加组队
     *
     * @param teamAddDTO 队伍信息
     * @return 主键Id
     */
    @ApiOperation(value = "添加组队")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDTO teamAddDTO, HttpServletRequest request){
        if(teamAddDTO == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(teamAddDTO,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 修改组队信息
     *
     * @param teamUpdateDTO 修改后的队伍信息
     * @return 修改是否成功
     */
    @ApiOperation(value = "修改组队信息")
    @PutMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateDTO teamUpdateDTO, HttpServletRequest request){
        if(teamUpdateDTO == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateDTO, loginUser);
//        if (!result) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
//        }
        return ResultUtils.success(result);
    }

    /**
     * 获取队伍信息
     *
     * @param teamId 队伍id
     * @return 获取的队伍信息
     */
    @ApiOperation(value = "获取队伍信息")
    @GetMapping("/get")
    public BaseResponse<TeamByIdVO> getTeamById(Long teamId) {
        //判断teamId是否符合要求
        judgeTeamId(teamId);
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return ResultUtils.success(BeanCopyUtils.copyBean(teamService.getById(teamId),TeamByIdVO.class));
    }

    /**
     * 获取队伍信息及成员信息
     *
     * @param teamId 队伍id
     * @return 返回队伍及成员信息
     */
    @ApiOperation(value = "获取队伍信息及成员信息")
    @GetMapping("/get/detail")
    public BaseResponse<TeamUserListVO> getTeamAndUser(Long teamId) {
        judgeTeamId(teamId);
        TeamUserListVO team = teamService.getTeamAndUser(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return ResultUtils.success(team);
    }


    /**
     * 删除队伍信息
     *
     * @param deleteDTO 队伍id
     * @return 判断是否删除成功
     */
    @ApiOperation(value = "删除队伍信息||解散队伍")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteDTO deleteDTO, HttpServletRequest request) {
        if (deleteDTO == null || deleteDTO.getId() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.deleteTeam(deleteDTO.getId(),loginUser));
    }

    /**
     * 查询队伍列表信息、但不包含已加入队伍的信息
     *
     * @param teamListDTO 查询条件
     * @return 获取的分页队伍数据
     */
    @ApiOperation(value = "分页获取队伍信息")
    @GetMapping("/list")
    public BaseResponse<PageVO> teamListByPage(TeamListDTO teamListDTO,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.teamList(teamListDTO,loginUser));
    }

    /**
     * 分页获取队伍信息
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param teamListDTO 查询条件
     * @return 获取的分页队伍数据
     */
    @ApiOperation(value = "查询队伍列表")
    @GetMapping("/list/page")
    public BaseResponse<PageVO> teamList(Integer pageNum, Integer pageSize, TeamListDTO teamListDTO,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.teamListPage(pageNum,pageSize,teamListDTO,loginUser));
    }

    /**
     * 用户加入队伍
     *
     * @param teamListDTO 队伍的主键和密码
     * @return 判断是否加入成功
     */
    @ApiOperation(value = "用户加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinDTO teamListDTO, HttpServletRequest request) {
        if (teamListDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamListDTO, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 用户退出队伍
     * @param teamId 队伍主键
     * @param request 获取当前登录用户
     * @return 判断是否退出成功
     */
    @ApiOperation(value = "用户退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(Long teamId, HttpServletRequest request) {
        judgeTeamId(teamId);
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamId, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 查询当前用户已加入的队伍
     *
     * @param request 获取当前登录用户
     * @return 返回已加入队伍列表
     */
    @ApiOperation(value = "用户已加入的队伍")
    @GetMapping("/list/join")
    public BaseResponse<List<JoinTeamListVO>> userJoinTeamList(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(teamService.userJoinTeamList(loginUser)) ;
    }


    /**
     * 获取最匹配的队伍信息
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param request 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    @ApiOperation(value = "获取最匹配的队伍信息")
    @GetMapping("/match")
    public BaseResponse matchTeam(Integer pageNum,Integer pageSize, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser.getTags()  == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"暂无匹配的队伍,请添加合适的标签");
        }
        PageVO matchTeams = teamService.getMatchTeams(pageNum, pageSize, loginUser);
        if (matchTeams == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR,"暂无匹配的队伍,请添加合适的标签");
        }
        return ResultUtils.success(matchTeams);
    }


    /**
     * 判断teamId是否符合要求
     * @param teamId 队伍id
     */
    private void judgeTeamId(Long teamId){
        if (teamId == null || teamId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求数据错误");
        }
    }
}
