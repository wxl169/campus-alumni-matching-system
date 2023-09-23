package org.wxl.alumniMatching.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.common.ErrorCode;
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
        List<MessageTeam> messageTeams = messageTeamMapper.selectTeamMessage(teamId);
        List<MessageTeamVO> messageTeamVOS = BeanCopyUtils.copyBeanList(messageTeams, MessageTeamVO.class);
        List<Long> userId = new ArrayList<>(10);
        messageTeams.forEach(messageTeam -> {
            userId.add(messageTeam.getSendUserId());
        });
        if(userId.size() == 0){
            return null;
        }
        List<User> users = userMapper.selectBatchIds(userId);
        messageTeamVOS = messageTeamVOS.stream().peek(messageTeam -> {
            for (User user : users) {
                if (messageTeam.getSendUserId().equals(user.getId())){
                    messageTeam.setSendUserName(user.getUsername());
                    messageTeam.setSendUserAvatar(user.getAvatarUrl());
                }
            }
        }).collect(Collectors.toList());
        return messageTeamVOS;
    }
}
