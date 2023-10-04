package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.MessageTeam;
import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 16956
 * @since 2023-09-17
 */
public interface IMessageTeamUserService extends IService<MessageTeamUser> {

    /**
     * 保持队伍成员的聊天记录
     * @param teamMessageId 队伍消息ids
     * @param loginUserId 当前在线的成员列表
     * @param messageTeam 消息内容
     * @return 是否成功
     */
    boolean saveTeamUserMessage(Long teamMessageId, Set<Long> loginUserId,MessageTeam messageTeam);

    /**
     * 删除群员指定的id
     *
     * @param teamId 队伍id
     * @param userId 用户id
     */
    boolean deleteTeamMessageUser(Long teamId, Long userId);

    /**
     * 将该队伍历史消息改为已读
     *
     * @param teamId 队伍id
     * @param userId 当前登录用户id
     * @return 是否修改成功
     */
     boolean updateTeamMessageUser(Long teamId,Long userId);
}
