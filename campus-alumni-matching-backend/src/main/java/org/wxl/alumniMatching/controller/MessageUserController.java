package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.dto.HistoryMessageDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
import org.wxl.alumniMatching.domain.vo.NotReadMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageUserService;
import org.wxl.alumniMatching.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.TreeSet;

/**
 * @author 16956
 */
@Api(tags = "用户消息模块")
@RestController
@RequestMapping("/message/user")
public class MessageUserController {
    @Resource
    private IMessageUserService messageUserService;
    @Resource
    private IUserService userService;
    /**
     * 根据好友的id查询之间的聊天记录
     *
     * @param historyMessageDTO 消息信息
     * @param request 当前登录用户
     * @return 聊天记录
     */
    @ApiOperation("查看历史记录")
    @GetMapping("/get")
    public BaseResponse<List<MessageUserVO>> getMessageById(HistoryMessageDTO historyMessageDTO, HttpServletRequest request){
        judgeFriendId(historyMessageDTO.getFriendId());
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageUserService.getMessageById(historyMessageDTO,loginUser));
    }

    /**
     * 清空指定用户的消息记录(单方面）
     *
     * @param friendId 好友id
     * @param request 当前登录用户
     * @return 是否清除成功
     */
    @ApiOperation("清空消息记录")
    @PutMapping("/delete")
    public BaseResponse<Boolean> deleteMessageById(Long friendId,HttpServletRequest request){
        judgeFriendId(friendId);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageUserService.deleteMessageById(friendId,loginUser));
    }

    /**
     * 根据用户id查看最近聊天记录
     *
     * @param friendId 好友id
     * @param request 当前登录用户
     * @return 最新消息列表
     */
    @ApiOperation("查看最近聊天记录")
    @GetMapping("/get/recently")
    public BaseResponse<List<MessageUserVO>> selectFriendMessage(Long friendId, HttpServletRequest request){
        judgeFriendId(friendId);
        User loginUser = userService.getLoginUser(request);
       return ResultUtils.success(messageUserService.getRecentMessage(friendId,loginUser));
    }


    /**
     * 修改信息的状态
     *
     * @param friendId 好友id
     * @param request 当前登录用户
     * @return 修改是否成功
     */
    @ApiOperation("修改信息的状态")
    @PutMapping("/update/messageStatus")
    public BaseResponse<Boolean> updateMessageStatus(Long friendId, HttpServletRequest request){
        judgeFriendId(friendId);
        User loginUser = userService.getLoginUser(request);
        messageUserService.updateMessageStatus(friendId,loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 获取所有信息（一次）
     *
     * @param request 当前登录用户
     * @return 未读信息列表
     */
    @ApiOperation("获取所有信息")
    @GetMapping("/get/notRead")
    public BaseResponse<TreeSet<NotReadMessageVO>> getAllNotReadMessage(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageUserService.getAllNotReadMessage(loginUser));
    }

    /**
     * 判断好友id是否符合要求
     *
     * @param friendId 好友id
     */
    private void judgeFriendId(Long friendId){
        if (friendId == null || friendId <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
    }
}
