package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.MessageTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageTeamVO;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 16956
 * @since 2023-09-16
 */
public interface IMessageTeamService extends IService<MessageTeam> {

    /**
     * 将队伍消息信息保存在数据库
     * @param loginUserId 当前在线列表
     * @param teamMessageVO 消息信息
     * @return 是否成功
     */
    boolean saveTeamMessage(Set<Long> loginUserId, TeamMessageVO teamMessageVO);
    /**
     * 根据用户id查看最近聊天记录
     *
     * @param teamId 队伍Id
     * @param loginUser 当前登录用户
     * @return 最新消息列表
     */
    List<MessageTeamVO> getRecentMessage(Long teamId, User loginUser);

    /**
     * 当前登录用户清空聊天记录
     *
     * @param teamId 队伍id
     * @param userId 当前登录用户
     * @return 是否删除成功
     */
    Boolean deleteTeamMessage(Long teamId, Long userId);
}
