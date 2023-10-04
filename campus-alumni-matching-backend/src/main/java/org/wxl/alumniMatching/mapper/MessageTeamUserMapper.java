package org.wxl.alumniMatching.mapper;

import org.apache.ibatis.annotations.Param;
import org.wxl.alumniMatching.domain.entity.MessageTeamUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.alumniMatching.domain.vo.MessageTeamVO;

import java.util.List;
import java.util.Set;

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
    boolean deleteMessageTeamUser(@Param("teamId") Long teamId,@Param("userId") Long userId);

    /**
     * 修改当前队伍该用户之前的消息记录为已读
     *
     * @param teamId 队伍id
     * @param userId 当前登录用户
     * @return 是否修改成功
     */
    boolean updateTeamMessageStatus(@Param("teamId") Long teamId,@Param("userId") Long userId);
}
