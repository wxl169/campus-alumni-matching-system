package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.domain.entity.MessageTeam;
import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageTeamVO;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.MessageTeamMapper;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IMessageTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.service.IMessageTeamUserService;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 16956
 * @since 2023-09-16
 */
@Service
public class MessageTeamServiceImpl extends ServiceImpl<MessageTeamMapper, MessageTeam> implements IMessageTeamService {
@Resource
private IMessageTeamUserService messageTeamUserService;
@Resource
private MessageTeamMapper messageTeamMapper;
@Resource
private UserMapper userMapper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTeamMessage(Set<Long> loginUserId, TeamMessageVO teamMessageVO) {
        MessageTeam messageTeam = new MessageTeam();
        messageTeam.setContent(teamMessageVO.getContent().toString());
        messageTeam.setSendTime(LocalDateTime.now());
        messageTeam.setSendUserId(teamMessageVO.getSendUserId());
        messageTeam.setTeamId(teamMessageVO.getTeamId());
        //存入队伍信息表
        int teamMessageId = messageTeamMapper.saveTeamMessage(messageTeam);
        if (teamMessageId <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"信息保持失败");
        }
        //存入队伍消息详情表
        boolean judge = messageTeamUserService.saveTeamUserMessage(messageTeam.getId(),loginUserId,messageTeam);
        if (!judge){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"信息保持失败");
        }
        return true;
    }


    @Override
    public List<MessageTeamVO> getRecentMessage(Long teamId, User loginUser) {
        //根据队伍id 查出最近两天的消息记录
        List<MessageTeam> messageTeams = messageTeamMapper.selectTeamMessage(teamId,loginUser.getId());
        List<MessageTeamVO> messageTeamVOS = BeanCopyUtils.copyBeanList(messageTeams, MessageTeamVO.class);
        //将发送消息的用户id存入集合中
        Set<Long> userId = new HashSet<>();
        messageTeams.forEach(messageTeam -> {
            userId.add(messageTeam.getSendUserId());
        });
        //如果没有用户则表示没有消息放松，返回空
        if(userId.size() == 0){
            return null;
        }

        //如果有则根据id查出用户的姓名和头像
        List<User> users = userMapper.selectBatchIds(userId);
        //遍历消息集合，将每条消息传入用户的头像和姓名
        messageTeamVOS = messageTeamVOS.stream().peek(messageTeam -> {
            for (User user : users) {
                if (messageTeam.getSendUserId().equals(user.getId())){
                    messageTeam.setSendUserName(user.getUsername());
                    messageTeam.setSendUserAvatar(user.getAvatarUrl());
                }
            }
            //如果是当前登录用户发送的信息，则将消息展示在页面的右侧
            if (messageTeam.getSendUserId().equals(loginUser.getId())){
                messageTeam.setCurrent(true);
            }
        }).collect(Collectors.toList());
        return messageTeamVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteTeamMessage(Long teamId, Long userId) {
        //删除当前用户接受到的消息
        boolean update = messageTeamUserService.deleteTeamMessageUser(teamId,userId);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除群消息失败");
        }
        //如果是当前用户发送的消息，则将字段message_show改为1
        LambdaUpdateWrapper<MessageTeam> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(userId != null && userId > 0 ,MessageTeam::getSendUserId,userId)
                .eq(teamId != null && teamId > 0,MessageTeam::getTeamId,teamId)
                .ne(MessageTeam::getIsDelete,MessageConstant.REMOVE_MESSAGE)
                .set(MessageTeam::getMessageShow,MessageConstant.REMOVE_MESSAGE);
        if (!this.update(updateWrapper)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除群消息失败");
        }
        return true;
    }


}
