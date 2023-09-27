package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.dto.HistoryTeamMessageDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageTeamVO;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageTeamService;
import org.wxl.alumniMatching.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 16956
 */
@Api(tags = "队伍消息模块")
@RestController
@RequestMapping("/message/team")
public class MessageTeamController {
    @Resource
    private IMessageTeamService messageTeamService;
    @Resource
    private IUserService userService;

    /**
     * 根据用户id查看最近聊天记录
     *
     * @param teamId 队伍Id
     * @param request 当前登录用户
     * @return 最新消息列表
     */
    @ApiOperation("查看最近聊天记录")
    @GetMapping("/get/recently")
    public BaseResponse<List<MessageTeamVO>> selectTeamMessage(Long teamId, HttpServletRequest request){
        judgeTeamId(teamId);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageTeamService.getRecentMessage(teamId,loginUser));
    }


    /**
     * 当前登录用户清空聊天记录
     *
     * @param teamId 队伍id
     * @param request 当前登录用户
     * @return 是否清除成功
     */
    @ApiOperation("清除聊天记录")
    @PutMapping("/delete")
    public BaseResponse<Boolean> deleteTeamMessage(Long teamId,HttpServletRequest request){
        judgeTeamId(teamId);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageTeamService.deleteTeamMessage(teamId,loginUser.getId()));
    }


    /**
     * 根据消息查询聊天记录
     *
     * @param historyTeamMessageDTO 条件
     * @param request 获取当前登录用户信息
     * @return 聊天记录
     */
    @ApiOperation("根据条件查询消息记录")
    @GetMapping("/get/oldMessage")
    public BaseResponse<List<TeamMessageVO>> selectTeamMessage(HistoryTeamMessageDTO historyTeamMessageDTO, HttpServletRequest request){
        judgeTeamId(historyTeamMessageDTO.getTeamId());
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageTeamService.getMessageByIdAndTime(historyTeamMessageDTO,loginUser));
    }


    /**
     * 判断队伍id是否符合要求
     *
     * @param teamId 队伍Id
     */
    private void judgeTeamId(Long teamId){
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
    }



}
