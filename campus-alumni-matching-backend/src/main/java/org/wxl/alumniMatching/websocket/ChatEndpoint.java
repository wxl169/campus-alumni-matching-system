package org.wxl.alumniMatching.websocket;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.config.GetHttpSessionConfig;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.contant.UserConstant;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageUserService;
import org.wxl.alumniMatching.utils.MessageUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 16956
 */
@ServerEndpoint(value = "/message",configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class ChatEndpoint {
    /**
     * 当前登录用户 用户id，session
     */
    private static final Map<Long,Session> ONLINE_USERS = new ConcurrentHashMap<>();

    /**
     * HttpSession对象中存储了当前登录用户的信息
     */
    private HttpSession httpSession;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private static IMessageUserService messageUserService;

    @Autowired
    public void setMessageUserService(IMessageUserService messageUserService){
        ChatEndpoint.messageUserService = messageUserService;
    }

    /**
     * 获取当前登录对象
     * @param config
     * @return
     */
    private User loginUser(EndpointConfig config){
        this.httpSession = (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
        return (User) this.httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
    }

    /**
     * 建立websocket连接后，被调用
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config){
        User user = loginUser(config);
        //当前登录用户
        ONLINE_USERS.put(user.getId(),session);

        //是否有暂存的消息，如果有则发送消息


        //当前在线列表
        for (Map.Entry<Long, Session> entry : ONLINE_USERS.entrySet()) {
            System.out.println(entry.getKey() + " "  + entry.getValue());
        }
    }


    /**
     * 广播消息
     * @param message
     */
    private void broadcastAllUsers(String message){
        //遍历map集合
        try {
            Set<Map.Entry<Long, Session>> entrySet = ONLINE_USERS.entrySet();
            for (Map.Entry<Long,Session> entry : entrySet) {
                //获取到所有用户对应的session对象
                Session session = entry.getValue();
                //发送消息
                session.getBasicRemote().sendText(message);
             }
            } catch (IOException e) {
            //记录日志
                log.error("广播消息有误："+e);
            }
    }


    /**
     *浏览器发送消息到服务端，该方法被调用
     *
     * @param message {"toName":"张三","message":"你好"}
     */
    @OnMessage
    public void onMessage(String message){
        if (message == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        try {
            //将消息推送给指定的用户
            Gson gson = new Gson();
            SendMessageDTO messageDTO = gson.fromJson(message, SendMessageDTO.class);

            if (messageDTO.getToUserId() == null || messageDTO.getToUserId() <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择接收人");
            }
            if (StringUtils.isBlank(messageDTO.getContent())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入内容");
            }

            //获取要将数据发送的用户
            Long toUserId = messageDTO.getToUserId();
            //获取消息数据
            String data = messageDTO.getContent();
            //获取当前登录用户
            User user = (User)httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            //获取推送给指定用户的消息格式的数据
            String resultMessage = MessageUtils.getMessage(MessageConstant.NOT_SYSTEM_MESSAGE, toUserId, data);

            //将消息保存在数据库中
            boolean sendMessage = messageUserService.sendMessage(messageDTO, user);
            if (!sendMessage){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"发送消息失败");
            }

            //如果发送消息对象没有在线
            if (ONLINE_USERS.get(toUserId) == null){
                //存入消息队列中

            }else{
                //发送消息对象在线
                ONLINE_USERS.get(toUserId).getBasicRemote().sendText(resultMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 断开websocket连接时被调用
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session){
        User user = (User)this.httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
        ONLINE_USERS.remove(user.getId());
    }


    /**
     * 发送错误时，该方法被调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        User user = (User)this.httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
        log.error("用户名：" +user.getUsername() + "————发送消息出错："+error);
        error.printStackTrace();
    }

}
