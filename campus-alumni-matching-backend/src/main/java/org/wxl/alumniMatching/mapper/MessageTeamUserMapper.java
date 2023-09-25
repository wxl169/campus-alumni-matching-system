package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 16956
 * @since 2023-09-17
 */
public interface MessageTeamUserMapper extends BaseMapper<MessageTeamUser> {

    /**
     * 删除当前用户在该群接受到的消息
     *
     * @param teamId 队伍id
     * @param userId 用户id
     * @return 是否删除成功
     */
    boolean deleteMessageTeamUser(Long teamId, Long userId);
}
