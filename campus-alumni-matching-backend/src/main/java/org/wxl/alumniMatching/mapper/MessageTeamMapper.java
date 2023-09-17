package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.MessageTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

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
     * @return 消息列表
     */
    List<MessageTeam> selectTeamMessage(Long teamId);
}
