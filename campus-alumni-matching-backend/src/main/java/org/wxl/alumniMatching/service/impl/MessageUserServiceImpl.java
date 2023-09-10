package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.MessageConstant;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (StringUtils.isBlank(sendMessageDTO.getContent())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请输入内容");
        }
        if (sendMessageDTO.getToUserId() == null || sendMessageDTO.getToUserId() <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请选择聊天对象");
        }
        judgeMessage(sendMessageDTO.getToUserId(),loginUser);
        MessageUser messageUser = new MessageUser();
        //将信息保存数据库
        messageUser.setContent(sendMessageDTO.getContent());
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
        judgeMessage(friendId,loginUser);
        //查看最近的消息记录
        List<MessageUser> messageUserList = messageUserMapper.selectRecentMessage(friendId,loginUser.getId());
        //将消息脱敏
        List<MessageUserVO> messageUserVOList = BeanCopyUtils.copyBeanList(messageUserList, MessageUserVO.class);

        //未读消息的id列表
        Set<Long> messageIdSet = new HashSet<>();
        //获取未读消息的id
        for (MessageUser messageUser:messageUserList) {
            if (messageUser.getStatus() == MessageConstant.NOT_READ_MESSAGE){
                messageIdSet.add(messageUser.getId());
            }
        }
        //修改未读消息的状态为已读
        if (messageIdSet.size() > 0){
            LambdaUpdateWrapper<MessageUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(MessageUser::getStatus,MessageConstant.READ_MESSAGE)
                    .in(MessageUser::getId,messageIdSet);
            this.update(updateWrapper);
        }

        return messageUserVOList;
    }

    @Override
    public void updateMessageStatus(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        //将对方发给自己的信息标识为已读
       boolean judge =  messageUserMapper.updateMessageStatus(friendId,loginUser.getId(),LocalDateTime.now());
       if (!judge){
           log.error("将对方发送给当前用户的信息标识为已读出错");
       }
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
