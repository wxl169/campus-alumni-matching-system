package org.wxl.alumniMatching.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.domain.entity.MessageTeam;
import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import org.wxl.alumniMatching.domain.vo.UserShowVO;
import org.wxl.alumniMatching.mapper.MessageTeamUserMapper;
import org.wxl.alumniMatching.mapper.TeamMapper;
import org.wxl.alumniMatching.mapper.UserTeamMapper;
import org.wxl.alumniMatching.service.IMessageTeamUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 16956
 * @since 2023-09-17
 */
@Service
public class MessageTeamUserServiceImpl extends ServiceImpl<MessageTeamUserMapper, MessageTeamUser> implements IMessageTeamUserService {
@Resource
private UserTeamMapper userTeamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTeamUserMessage(Long teamMessageId,Set<Long> loginUserId, MessageTeam messageTeam) {
        //队伍id
        Long teamId = messageTeam.getTeamId();
        //该队伍的所有成员id
        List<Long> userList = userTeamMapper.getTeamUserId(teamId);
        //如果该队伍只有群主一个人，则直接返回
        if (userList.size() == 1){
            return true;
        }

        //筛选出没有登录的用户
        Set<Long> finalLoginUserId = loginUserId;
        Set<Long> unLoginUserId = userList.stream().filter(userId -> {
            for (Long userId2 : finalLoginUserId) {
                if (userId.equals(userId2)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toSet());
        //排除发送消息的id
        loginUserId = loginUserId.stream().filter(loginUserId2 ->{
            return !loginUserId2.equals(messageTeam.getSendUserId());
        }).collect(Collectors.toSet());

        List<MessageTeamUser> messageTeamUsers = new ArrayList<>(10);

            MessageTeamUser messageTeamUser = null;
            //已登录的用户，将状态改为已读
            for (Long userId : loginUserId) {
                messageTeamUser = new MessageTeamUser();
                messageTeamUser.setReceiveUserId(userId);
                messageTeamUser.setStatus(MessageConstant.READ_MESSAGE);
                messageTeamUser.setMessageTeamId(teamMessageId);
                messageTeamUser.setSendTime(messageTeam.getSendTime());
                messageTeamUsers.add(messageTeamUser);
            }
            for (Long userId : unLoginUserId) {
                messageTeamUser = new MessageTeamUser();
                messageTeamUser.setReceiveUserId(userId);
                messageTeamUser.setStatus(MessageConstant.NOT_READ_MESSAGE);
                messageTeamUser.setMessageTeamId(teamMessageId);
                messageTeamUser.setSendTime(messageTeam.getSendTime());
                messageTeamUsers.add(messageTeamUser);
            }

        return this.saveBatch(messageTeamUsers);
    }
}
