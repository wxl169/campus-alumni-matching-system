package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.MessageUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.alumniMatching.domain.entity.User;

import java.time.LocalDateTime;
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
    int deleteMessageById(Long friendId, Long userId);

    /**
     * 查看用户之间最近的聊天记录
     *
     * @param friendId 好友id
     * @param userId 当前用户id
     * @return 最近消息列表
     */
    List<MessageUser> selectRecentMessage(Long friendId, Long userId);
    /**
     * 修改信息的状态
     *
     * @param friendId 好友id
     * @param userId 当前登录用户
     * @param now 当前时间
     * @return 修改是否成功
     */
    boolean updateMessageStatus(Long friendId, Long userId, LocalDateTime now);

    /**
     * 根据传来的内容信息，模糊匹配消息记录
     *
     * @param content 消息
     * @param friendId 好友id
     * @param sendTime 发送时间
     * @param userId 用户Id
     * @return 消息记录
     */
    List<MessageUser> getMessageByContent(String content,String sendTime, Long friendId, Long userId);

    /**
     * 获取所有的消息记录，然后按时间顺序降序排序
     *
     * @param userId 当前登录用户
     * @return 消息列表
     */
    List<MessageUser> getAllNotReadMessage(Long userId);
}
