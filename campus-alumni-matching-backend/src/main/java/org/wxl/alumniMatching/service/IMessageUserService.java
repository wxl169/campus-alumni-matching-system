package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.dto.MessageUserSendDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;

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
     * @param messageUserSendDTO 消息信息
     * @param loginUser 当前登录用户
     * @return 是否发送成功
     */
    boolean sendMessage(MessageUserSendDTO messageUserSendDTO, User loginUser);

    /**
     * 根据好友的id查询之间的聊天记录
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 聊天记录
     */
    List<MessageUser> getMessageById(Long friendId, User loginUser);

    /**
     * 清空指定用户的消息记录(单方面）
     *
     * @param friendId 好友id
     * @param loginUser 当前登录用户
     * @return 是否清除成功
     */
    boolean deleteMessageById(Long friendId, User loginUser);
}