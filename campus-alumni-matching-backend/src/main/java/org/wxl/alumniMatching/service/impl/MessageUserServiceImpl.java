package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.domain.dto.MessageUserSendDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.MessageUserMapper;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IMessageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  用户消息服务实现类
 * </p>
 *
 * @author 16956
 */
@Service
public class MessageUserServiceImpl extends ServiceImpl<MessageUserMapper, MessageUser> implements IMessageUserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageUserMapper messageUserMapper;


    /**
     * 给用户发送消息
     *
     * @param messageUserSendDTO 消息信息
     * @param loginUser 当前登录用户
     * @return 是否发送成功
     */
    @Override
    public boolean sendMessage(MessageUserSendDTO messageUserSendDTO, User loginUser) {
        if (messageUserSendDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        if (StringUtils.isBlank(messageUserSendDTO.getContent())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请输入内容");
        }
        judgeMessage(messageUserSendDTO.getReceiveUserId(),loginUser);
        MessageUser messageUser = BeanCopyUtils.copyBean(messageUserSendDTO, MessageUser.class);
        //将信息保存数据库
        messageUser.setSendUserId(loginUser.getId());
        messageUser.setStatus(0);
        messageUser.setSendTime(LocalDateTime.now());
        messageUser.setMessageShow(0);
        boolean save = this.save(messageUser);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息发送失败");
        }
        return true;
    }


    /**
     * 根据好友的id查询之间的聊天记录
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 聊天记录
     */
    @Override
    public List<MessageUser> getMessageById(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        return messageUserMapper.getMessageById(friendId,loginUser.getId());
    }

    /**
     * 清空指定用户的消息记录(单方面）
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 是否清除成功
     */
    @Override
    public boolean deleteMessageById(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        boolean delete = messageUserMapper.deleteMessageById(friendId,loginUser.getId());
        if (!delete){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"清空消息记录失败");
        }
        return true;
    }


    /**
     * 判断传入的消息数据是否符合要求
     */
    private void judgeMessage(Long friendId,User user){
        if (friendId == null || friendId <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请选择发送对象");
        }
        if (friendId.equals(user.getId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止给自己发信息");
        }
        //查看接收者是否在数据库中
        User friend = userMapper.selectById(friendId);
        if (friend == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该接收对象不存在");
        }
    }
}
