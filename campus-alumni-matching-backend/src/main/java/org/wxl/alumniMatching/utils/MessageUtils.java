package org.wxl.alumniMatching.utils;


import com.google.gson.Gson;
import org.wxl.alumniMatching.domain.vo.MessageVO;

import java.time.LocalDateTime;

/**
 * 封装json格式消息的工具类
 * @author 16956
 */
public class MessageUtils {
    /**
     *
     * @param isSysteamMessage 是否为系统消息 0——不是，1——是
     * @param id 发送消息的用户id / 发送消息的队伍id
     * @param message 发送的消息
     * @return json格式的消息
     */
    public static  String getMessage(Integer isSysteamMessage,Long userId, Long id, Object message, LocalDateTime dateTime){
        MessageVO messageVO = new MessageVO();
        messageVO.setIsSystem(isSysteamMessage);
        messageVO.setSendUserId(userId);
        messageVO.setContent(message);
        messageVO.setSendTime(dateTime);
        if (id!=null && id > 0){
            messageVO.setReceiveUserId(id);
        }
        //把字符串转成json格式的字符串
        Gson gson = new Gson();
        return gson.toJson(messageVO);
    }
}
