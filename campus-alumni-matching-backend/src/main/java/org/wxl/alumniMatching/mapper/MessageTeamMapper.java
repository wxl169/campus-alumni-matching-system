package org.wxl.alumniMatching.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.alumniMatching.domain.entity.MessageTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import org.wxl.alumniMatching.domain.vo.MessageTeamLogVO;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 16956
 * @since 2023-09-16
 */
public interface MessageTeamMapper extends BaseMapper<MessageTeam> {

    /**
     * 保存队伍消息信息
     *
     * @param messageTeam 消息信息
     * @return 返回主键id
     */
    int saveTeamMessage(MessageTeam messageTeam);

    /**
     * 查出最近两天的消息记录
     *
     * @param teamId 队伍id
     * @param userId 用户id
     * @return 消息列表
     */
    List<MessageTeam> selectTeamMessage(@Param("teamId") Long teamId,@Param("userId") Long userId);

    /**
     * 查询当前队伍所有消息记录
     *
     * @param teamId 队伍id
     * @param userId 当前登录用户id
     * @return 消息列表
     */
    List<MessageTeam> selectTeamMessageAll(@Param("teamId") Long teamId,@Param("userId") Long userId);

    /**
     * 根据发送时间或内容查询聊天记录
     *
     * @param sendTime 发送时间
     * @param content 搜索内容
     * @param teamId  队伍id
     * @param userId 用户id
     * @return 消息记录列表
     */
    List<MessageTeam> selectTeamMessageBySendTime(@Param("sendTime") String sendTime,@Param("content") String content,@Param("teamId") Long teamId,@Param("userId") Long userId);

    /**
     * 查询当前队伍集合最新消息
     *
     * @param teamIds 队伍集合
     * @param loginUserId 当前登录用户id
     * @return 返回当前队伍集合最新消息
     */
    List<MessageTeamLogVO> selectRecentMessage(Set<Long> teamIds, @Param("loginUserId") Long loginUserId);
}
