package org.wxl.alumniMatching.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.domain.dto.HistoryMessageDTO;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
import org.wxl.alumniMatching.domain.vo.NotReadMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.MessageUserMapper;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IMessageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<MessageUserVO> getMessageById(HistoryMessageDTO historyMessageDTO, User loginUser) {
        if (historyMessageDTO.getFriendId() == null || historyMessageDTO.getFriendId()  <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请选择发送对象");
        }
        Long friendId = historyMessageDTO.getFriendId();
        if (friendId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止给自己发信息");
        }
        //查看接收者是否在数据库中
        User friend = userMapper.selectById(friendId);
        if (friend == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该接收对象不存在");
        }

        //如果没有条件则查询全部消息
        List<MessageUser> messageUserList = new ArrayList<>();
        if (StringUtils.isBlank(historyMessageDTO.getContent()) && historyMessageDTO.getSendTime() == null){
            messageUserList = messageUserMapper.getMessageById(historyMessageDTO.getFriendId(), loginUser.getId());
        }
        //如果设置了搜索内容
        if (StringUtils.isNotBlank(historyMessageDTO.getContent())){
            messageUserList = messageUserMapper.getMessageByContent(historyMessageDTO.getContent(),null,historyMessageDTO.getFriendId(),loginUser.getId());
        }

        //如果设置了时间
        if (StringUtils.isNotBlank(historyMessageDTO.getSendTime())){
            messageUserList = messageUserMapper.getMessageByContent(null,historyMessageDTO.getSendTime(),historyMessageDTO.getFriendId(),loginUser.getId());
        }
        //加入发送和接受用户名
        //获取好友名
        String friendName = friend.getUsername();
        //添加
        List<MessageUserVO> messageUserVOS = BeanCopyUtils.copyBeanList(messageUserList, MessageUserVO.class);
        List<MessageUserVO> userVOS = messageUserVOS.stream().map(message -> {
            if (friendId.equals(message.getSendUserId())) {
                message.setSendUserName(friendName);
                message.setReceiveUserName(loginUser.getUsername());
            } else {
                message.setSendUserName(loginUser.getUsername());
                message.setReceiveUserName(friendName);
            }
            return message;
        }).collect(Collectors.toList());
        return  userVOS;
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

    @Override
    public List<NotReadMessageVO> getAllNotReadMessage(User loginUser) {
        //获取所有的消息记录，未读的展示在最前面，然后按时间顺序降序排序
        //1.获取所有的消息记录，然后时间降序排列
        Long loginUserId = loginUser.getId();
        List<MessageUser> messageUserList = messageUserMapper.getAllNotReadMessage(loginUserId);
        messageUserList = messageUserList.stream().filter(message ->{
            //排除空数据
            return message != null;
        }).collect(Collectors.toList());
        //如果为空，则返回null
        if (messageUserList.size() == 0){
            return null;
        }else if (messageUserList.size() > 1){
            //如果是一条以上，则需要筛选出是发送人和接收人相互发送的信息，并选出最近时间的信息
            // 由于正序可能会导致后面的元素会向前移动，导致索引发生变化，可能会导致未能正确地遍历所有元素，所有选择倒序
            for (int i = messageUserList.size() - 1; i > 0; i--){
                for (int j = i - 1; j >= 0; j--){
                    MessageUser messageUser = messageUserList.get(j);
                    Long sendUserId = messageUser.getSendUserId();
                    Long receiveUserId = messageUser.getReceiveUserId();
                    LocalDateTime sendTime = messageUser.getSendTime();
                    MessageUser messageUser2 = messageUserList.get(i);
                    Long sendUserId2 = messageUser2.getSendUserId();
                    Long receiveUserId2 = messageUser2.getReceiveUserId();
                    LocalDateTime sendTime2 = messageUser2.getSendTime();
                    //如果是同一组用户互发消息，则选出发送时间最接近当前时间的一组
                    if (sendUserId.equals(receiveUserId2) && sendUserId2.equals(receiveUserId)){
                        //选出时间最大的一组数据
                        if (sendTime.isAfter(sendTime2)){
                            messageUserList.remove(i);
                        }else {
                            messageUserList.remove(j);
                        }
                        //如果删除了数组，则换上一个数组轮询
                        break;
                    }
                }
            }
        }
        List<NotReadMessageVO> notReadMessageVOS = BeanCopyUtils.copyBeanList(messageUserList, NotReadMessageVO.class);
        //注入用户名，头像
        notReadMessageVOS = notReadMessageVOS.stream().peek(message ->{
            User user;
            //如果是当前登录用户发送
            if (loginUserId.equals(message.getSendUserId())){
                //将接收人的名字,头像输入
                 user = userMapper.selectNameAndAvatar(message.getReceiveUserId());
                 message.setReceiveUserName(user.getUsername());
                 message.setReceiveUserAvatar(user.getAvatarUrl());
            }else{
                //将发送人的名字和头像输入
                user = userMapper.selectNameAndAvatar(message.getSendUserId());
                message.setSendUserName(user.getUsername());
                message.setSendUserAvatar(user.getAvatarUrl());
            }
        }).collect(Collectors.toList());
        return notReadMessageVOS;
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
