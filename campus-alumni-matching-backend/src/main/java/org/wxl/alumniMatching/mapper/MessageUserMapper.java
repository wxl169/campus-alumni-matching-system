package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.MessageUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 16956
 * @since 2023-08-29
 */
public interface MessageUserMapper extends BaseMapper<MessageUser> {

    /**
     * 根据好友的id查询之间的聊天记录
     *
     * @param friendId 好友id
     * @param userId 当前登录用户
     * @return 聊天记录
     */
    List<MessageUser> getMessageById(Long friendId, Long userId);

    /**
     * 清空指定用户的消息记录(单方面）
     *
     * @param friendId 好友id
     * @param userId 当前登录用户
     * @return 是否清除成功
     */
    boolean deleteMessageById(Long friendId, Long userId);
}
