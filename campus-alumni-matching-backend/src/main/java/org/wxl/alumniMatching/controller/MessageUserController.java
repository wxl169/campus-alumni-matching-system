package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageUserService;
import org.wxl.alumniMatching.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * @param friendId 好友id
     * @param request 当前登录用户
     * @return 聊天记录
     */
    @ApiOperation("查看历史记录")
    @GetMapping("/get")
    public BaseResponse<List<MessageUser>> getMessageById(Long friendId,HttpServletRequest request){
        judgeFriendId(friendId);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(messageUserService.getMessageById(friendId,loginUser));
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
    @PutMapping("/get/recently")
    public BaseResponse<List<MessageUserVO>> selectFriendMessage(Long friendId, HttpServletRequest request){
        judgeFriendId(friendId);
        User loginUser = userService.getLoginUser(request);
       return ResultUtils.success(messageUserService.getRecentMessage(friendId,loginUser));
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
