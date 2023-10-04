package org.wxl.alumniMatching.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.domain.dto.HistoryMessageDTO;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.*;
import org.wxl.alumniMatching.domain.vo.MessageTeamLogVO;
import org.wxl.alumniMatching.domain.vo.MessageTeamVO;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
import org.wxl.alumniMatching.domain.vo.NotReadMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.*;
import org.wxl.alumniMatching.service.IMessageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.utils.BeanCopyUtils;
import org.wxl.alumniMatching.websocket.ChatEndpoint;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Resource
    private MessageTeamMapper messageTeamMapper;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public boolean sendMessage(SendMessageDTO sendMessageDTO, User loginUser) {
        if (sendMessageDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        if (StringUtils.isBlank(sendMessageDTO.getContent())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请输入内容");
        }
        if (sendMessageDTO.getReceiveUserId() == null || sendMessageDTO.getReceiveUserId() <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请选择聊天对象");
        }
        judgeMessage(sendMessageDTO.getReceiveUserId(),loginUser);
        MessageUser messageUser = new MessageUser();
        //将信息保存数据库
        messageUser.setContent(sendMessageDTO.getContent());
        messageUser.setSendUserId(loginUser.getId());
        messageUser.setReceiveUserId(sendMessageDTO.getReceiveUserId());
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择好友");
        }
        //查看接收者是否在数据库中
        User friend = userMapper.selectById(friendId);
        if (friend == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该接收对象不存在");
        }
        List<MessageUser> messageUserList = new ArrayList<>();
        List<MessageUser> messageUserList1 = null;
        List<MessageUser> messageUserList2 = null;
        List<MessageUser> messageUserList3 = new ArrayList<>();
        //查询redis中数据
        String readKey = String.format("alumniMatching:message:onMessage:%s", loginUser.getId().toString());
        //将自己发送的消息提取出来
        if (redisTemplate.opsForHash().hasKey(readKey,friendId.toString())){
             messageUserList1 = (List<MessageUser>) redisTemplate.opsForHash().get(readKey,friendId.toString());
        }
        //获取好友发送的消息
        String readKey2 = String.format("alumniMatching:message:onMessage:%s", friendId.toString());
        //将自己发送的消息提取出来
        if (redisTemplate.opsForHash().hasKey(readKey2,loginUser.getId().toString())){
            messageUserList2 = (List<MessageUser>) redisTemplate.opsForHash().get(readKey2,loginUser.getId().toString());
        }

        if (messageUserList1 != null){
            messageUserList3.addAll(messageUserList1);
        }
        if (messageUserList2 != null){
            messageUserList3.addAll(messageUserList2);
        }
        //将数据按时间大小从小到大排序
        messageUserList3 = messageUserList3.stream().sorted(Comparator.comparing(MessageUser::getSendTime)).collect(Collectors.toList());

        //如果没有条件则查询全部消息
        if (StringUtils.isBlank(historyMessageDTO.getContent()) && historyMessageDTO.getSendTime() == null){
            messageUserList.addAll(messageUserMapper.getMessageById(historyMessageDTO.getFriendId(), loginUser.getId()));
        }
        //如果设置了搜索内容
        if (StringUtils.isNotBlank(historyMessageDTO.getContent())){
            //筛选出redis中的数据
            messageUserList3 = messageUserList3.stream().filter(messageUser -> {
                return messageUser.getContent().contains(historyMessageDTO.getContent());
            }).collect(Collectors.toList());
            messageUserList.addAll(messageUserMapper.getMessageByContent(historyMessageDTO.getContent(),null,historyMessageDTO.getFriendId(),loginUser.getId()));
        }

        //如果设置了时间
        if (StringUtils.isNotBlank(historyMessageDTO.getSendTime())){
            //筛选出redis中的数据
            messageUserList3 = messageUserList3.stream().filter(messageUser -> {
                return messageUser.getSendTime().toLocalDate().equals(LocalDate.parse(historyMessageDTO.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE));
            }).collect(Collectors.toList());
            messageUserList.addAll(messageUserMapper.getMessageByContent(null,historyMessageDTO.getSendTime(),historyMessageDTO.getFriendId(),loginUser.getId()));
        }
        messageUserList.addAll(messageUserList3);
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
    @Transactional(rollbackFor = Exception.class)
    public List<MessageUserVO> getRecentMessage(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        //查看用户与用户之间最近的消息记录
        List<MessageUser> messageUserList = messageUserMapper.selectRecentMessage(friendId,loginUser.getId());

        //清除redis中Set数据结构记录的 —— 哪些用户给当前登录用户发送了消息
        String sendMessageUserKey = String.format("alumniMatching:message:sendMessageUser:%s",loginUser.getId().toString());
        //获取当前登录用户发送的消息
        String readKey = String.format("alumniMatching:message:onMessage:%s", loginUser.getId().toString());
        //如果没有存入则不做处理
        if (redisTemplate.opsForSet().size(sendMessageUserKey) != 0){
            //如果发送消息对象不在线
            if (ChatEndpoint.ONLINE_USERS.get(friendId) == null){
                //删除redis中存的该id
                redisTemplate.opsForSet().remove(sendMessageUserKey,friendId);
            }else {
                //如果在线，则将消息改为已读
                updateMessageStatus(friendId, loginUser);
            }
        }

        //将未读的消息变为已读
        boolean b = messageUserMapper.updateMessageStatus(friendId, loginUser.getId(), LocalDateTime.now());
        if (!b){
            log.error("更新用户消息状态失败!!!!!!!!!!");
        }
        //将自己发送的消息提取出来
        if (redisTemplate.opsForHash().hasKey(readKey,friendId.toString())){
            List<MessageUser> messageUserList1 = (List<MessageUser>) redisTemplate.opsForHash().get(readKey,friendId.toString());
            if (messageUserList1 != null){
                //添加到集合中
                messageUserList.addAll(messageUserList1);
            }
        }
        //获取好友发送的消息
        String readKey2 = String.format("alumniMatching:message:onMessage:%s", friendId.toString());
        //将自己发送的消息提取出来
        if (redisTemplate.opsForHash().hasKey(readKey2,loginUser.getId().toString())){
            List<MessageUser> messageUserList1 = (List<MessageUser>) redisTemplate.opsForHash().get(readKey2,loginUser.getId().toString());
            if (messageUserList1 != null){
                //添加到集合中
                messageUserList.addAll(messageUserList1);
            }
        }
        // 定义一个Comparator对象来定义排序规则
        Comparator<MessageUser> comparator = Comparator.comparing(MessageUser::getSendTime);
        // 创建一个TreeSet，并传入Comparator对象
        messageUserList = messageUserList.stream().sorted(comparator).collect(Collectors.toList());

        //将消息脱敏
        return BeanCopyUtils.copyBeanList(messageUserList, MessageUserVO.class);
    }

    @Override
    public void updateMessageStatus(Long friendId, User loginUser) {
        judgeMessage(friendId,loginUser);
        //将对方发给自己的信息标识为已读
        String readKey = String.format("alumniMatching:message:onMessage:%s", friendId.toString());
        if (redisTemplate.opsForHash().hasKey(readKey,loginUser.getId().toString())){
            //将消息记录标记为已读
            List<MessageUser> messageUserList = (List<MessageUser>) redisTemplate.opsForHash().get(readKey,loginUser.getId().toString());
            if (messageUserList != null && messageUserList.size() != 0){
                messageUserList = messageUserList.stream().map(messageUser -> {
                    messageUser.setStatus(MessageConstant.READ_MESSAGE);
                    return messageUser;
                }).collect(Collectors.toList());
                redisTemplate.opsForHash().put(readKey,loginUser.getId().toString(),messageUserList);
            }
        }
    }

    @Override
    public TreeSet<NotReadMessageVO> getAllNotReadMessage(User loginUser) {
        Long loginUserId = loginUser.getId();

        //存入——存在与redis中的数据
        List<MessageUser> messageUsers = new ArrayList<>();
        String sendMessageUserKey = String.format("alumniMatching:message:sendMessageUser:%s",loginUser.getId().toString());
        //如果redis中没有存入则跳过
        Set<Integer> ids = new HashSet<>();
        if (redisTemplate.opsForSet().size(sendMessageUserKey) != 0){
            ids= redisTemplate.opsForSet().members(sendMessageUserKey);
        }
        if (ids != null && ids.size() != 0){
            //如果ids有数据则根据ids中的id查询redis
            ids.forEach(id ->{
                String readKey = String.format("alumniMatching:message:onMessage:%s", id.toString());
                List<MessageUser> messageUserList = (List<MessageUser>) redisTemplate.opsForHash().get(readKey,loginUser.getId().toString());
                if (messageUserList != null){
                    //选取时间最大的数据
                    Optional<MessageUser> maxMessageUser = messageUserList.stream()
                            .max(Comparator.comparing(MessageUser::getSendTime));
                    //添加到集合中
                    messageUsers.add(maxMessageUser.get());
                }
            });
        }

        //获取所有的消息记录，未读的展示在最前面，然后按时间顺序降序排序
        //1.获取所有的消息记录，然后时间降序排列
        List<MessageUser> messageUserList = messageUserMapper.getAllNotReadMessage(loginUserId);
        //排除空数据
        messageUserList = messageUserList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        //如果为空，则返回null
        List<NotReadMessageVO> notReadMessageVOS = null;
        if (messageUserList.size() > 1){
            //如果是一条以上，则需要筛选出是发送人和接收人相互发送的信息，并选出最近时间的信息
            // 由于正序可能会导致后面的元素会向前移动，导致索引发生变化，可能会导致未能正确地遍历所有元素，所有选择倒序
            for (int i = messageUserList.size() - 1; i > 0; i--){
                for (int j = i - 1; j >= 0; j--){
                    //第一组消息
                    MessageUser messageUser = messageUserList.get(j);
                    Long sendUserId = messageUser.getSendUserId();
                    Long receiveUserId = messageUser.getReceiveUserId();
                    LocalDateTime sendTime = messageUser.getSendTime();
                    //第二组消息
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
            messageUserList =  messageUserList.stream().filter(messageUser -> {
                for (int i = 0; i < messageUsers.size(); i++){
                    if ((messageUser.getSendUserId().equals(messageUsers.get(i).getSendUserId()) && messageUser.getReceiveUserId().equals(messageUsers.get(i).getReceiveUserId()) )
                    || (messageUser.getReceiveUserId().equals(messageUsers.get(i).getSendUserId()) && messageUser.getSendUserId().equals(messageUsers.get(i).getReceiveUserId()))){
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
            messageUserList.addAll(messageUsers);

            //存入用户之间的聊天记录
            notReadMessageVOS = BeanCopyUtils.copyBeanList(messageUserList, NotReadMessageVO.class);
            //注入用户名，头像
            notReadMessageVOS = notReadMessageVOS.stream().peek(message ->{
                User user;
                //如果是当前登录用户发送
                if (loginUserId.equals(message.getSendUserId())){
                    //将接收人的名字,头像输入
                    user = userMapper.selectNameAndAvatar(message.getReceiveUserId());
                    message.setReceiveUserName(user.getUsername());
                    message.setReceiveUserAvatar(user.getAvatarUrl());
                    message.setStatus(MessageConstant.READ_MESSAGE);
                }else{
                    //将发送人的名字和头像输入
                    user = userMapper.selectNameAndAvatar(message.getSendUserId());
                    message.setSendUserName(user.getUsername());
                    message.setSendUserAvatar(user.getAvatarUrl());
                }
            }).collect(Collectors.toList());
        }
        //-----------------------处理队伍消息------------------------
        //找出当前用户加入群聊中最后一次发言
        //1. 找出当前用户加入的队伍
        Set<Long> teamIds = teamMapper.selectUserJoinTeamId(loginUserId);
        List<NotReadMessageVO> collect = null;
        //2. 如果当前用户没有加入队伍，则只返回与用户之间的消息，如果有则查询队伍中的消息记录
        if (teamIds.size() != 0){
            //3. 根据队伍id查询最近的聊天记录，如果没有则不显示
            List<MessageTeamLogVO> messageTeamUsers = messageTeamMapper.selectRecentMessage(teamIds,loginUserId);
            //4. 根据send_user_id判断 如果发送人是本人 则 表示已读，如果不是本人则判断读未读
                collect = messageTeamUsers.stream().map(messageTeamLogVO -> {
                NotReadMessageVO notReadMessageVO = new NotReadMessageVO();
                notReadMessageVO.setSendTime(messageTeamLogVO.getSendTime());
                notReadMessageVO.setContent(messageTeamLogVO.getContent());
                notReadMessageVO.setStatus(messageTeamLogVO.getStatus());
                notReadMessageVO.setTeamId(messageTeamLogVO.getTeamId());
                notReadMessageVO.setSendUserId(messageTeamLogVO.getSendUserId());
                //查询队伍名
                Team team = teamMapper.selectTeamNameAndAvatar(messageTeamLogVO.getTeamId());
                notReadMessageVO.setTeamAvatar(team.getAvatarUrl());
                notReadMessageVO.setTeamName(team.getTeamName());
                //查询发送消息信息
                 User user = userMapper.selectNameAndAvatar(messageTeamLogVO.getSendUserId());
                 notReadMessageVO.setSendUserName(user.getUsername());
                 notReadMessageVO.setSendUserAvatar(user.getAvatarUrl());
                return notReadMessageVO;
            }).collect(Collectors.toList());
        }
        // 定义一个Comparator对象来定义排序规则
        Comparator<NotReadMessageVO> comparator = (o1, o2) -> {
            // 按照send_time从大到小排序
            return o2.getSendTime().compareTo(o1.getSendTime());
        };
        // 创建一个TreeSet，并传入Comparator对象
        TreeSet<NotReadMessageVO> treeSet = new TreeSet<>(comparator);
        // 添加元素到treeSet

        if (notReadMessageVOS != null && notReadMessageVOS.size() != 0){
            treeSet.addAll(notReadMessageVOS);
        }
        if (collect != null && collect.size() != 0){
            treeSet.addAll(collect);
        }
        return treeSet;
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
