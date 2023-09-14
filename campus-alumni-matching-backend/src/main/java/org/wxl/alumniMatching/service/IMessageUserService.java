package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.domain.dto.HistoryMessageDTO;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageUserVO;
import org.wxl.alumniMatching.domain.vo.NotReadMessageVO;

import java.util.List;

/**
 * <p>
 *  用户消息表
 * </p>
 *
 * @author 16956
 */
public interface IMessageUserService extends IService<MessageUser> {

    /**
     * 给用户发送消息
     *
     * @param sendMessageDTO 消息信息
     * @param loginUser 当前登录用户
     * @return 是否发送成功
     */
    boolean sendMessage(SendMessageDTO sendMessageDTO, User loginUser);

    /**
     * 根据条件查询好友之间的聊天记录
     *
     * @param historyMessageDTO 消息信息
     * @param loginUser 当前登录用户
     * @return 聊天记录
     */
    List<MessageUserVO> getMessageById(HistoryMessageDTO historyMessageDTO, User loginUser);

    /**
     * 清空指定用户的消息记录(单方面）
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 是否清除成功
     */
    boolean deleteMessageById(Long friendId, User loginUser);

    /**
     * 根据用户id查看最近聊天记录
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 最新消息列表
     */
    List<MessageUserVO> getRecentMessage(Long friendId, User loginUser);
    /**
     * 修改信息的状态
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 修改是否成功
     */
    void updateMessageStatus(Long friendId, User loginUser);

    /**
     * 获取所有信息
     *
     * @param loginUser 当前登录用户
     * @return 未读信息列表
     */
    List<NotReadMessageVO> getAllNotReadMessage(User loginUser);
}
