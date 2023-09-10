package org.wxl.alumniMatching.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
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


    @Override
    public boolean sendMessage(SendMessageDTO sendMessageDTO, User loginUser) {
        if (sendMessageDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        if (StringUtils.isBlank(sendMessageDTO.getMessage())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请输入内容");
        }
        if (sendMessageDTO.getToUserId() == null || sendMessageDTO.getToUserId() <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请选择聊天对象");
        }
        judgeMessage(sendMessageDTO.getToUserId(),loginUser);
        MessageUser messageUser = new MessageUser();
        //将信息保存数据库
        messageUser.setContent(sendMessageDTO.getMessage());
        messageUser.setSendUserId(loginUser.getId());
        messageUser.setReceiveUserId(sendMessageDTO.getToUserId());
        messageUser.setStatus(0);
        messageUser.setSendTime(LocalDateTime.now());
        messageUser.setMessageShow(0);
        messageUser.setIsSystem(0);
        boolean save = this.save(messageUser);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息发送失败");
        }
        return true;
    }


    @Override
    public List<MessageUser> getMessageById(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        return messageUserMapper.getMessageById(friendId,loginUser.getId());
    }


    @Override
    public boolean deleteMessageById(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        int delete = messageUserMapper.deleteMessageById(friendId,loginUser.getId());
        if (delete == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"无消息可清除");
        }
        return true;
    }

    @Override
    public List<MessageUserVO> getRecentMessage(Long friendId, User loginUser) {

        return null;
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
