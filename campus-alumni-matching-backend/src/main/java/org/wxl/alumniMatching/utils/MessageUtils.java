package org.wxl.alumniMatching.utils;


import com.google.gson.Gson;
import org.wxl.alumniMatching.domain.vo.MessageVO;

/**
 * 封装json格式消息的工具类
 * @author 16956
 */
public class MessageUtils {
    /**
     *
     * @param isSysteamMessage 是否为系统消息 0——不是，1——是
     * @param fromUserId 发送消息的用户id
     * @param message 发送的消息
     * @return json格式的消息
     */
    public static  String getMessage(Integer isSysteamMessage,Long fromUserId,Object message){
        MessageVO messageVO = new MessageVO();
        messageVO.setIsSystem(isSysteamMessage);
        messageVO.setContent(message);
        if (fromUserId!=null && fromUserId > 0){
            messageVO.setReceiveUserId(fromUserId);
        }
        //把字符串转成json格式的字符串
        Gson gson = new Gson();
        return gson.toJson(messageVO);
    }
}
