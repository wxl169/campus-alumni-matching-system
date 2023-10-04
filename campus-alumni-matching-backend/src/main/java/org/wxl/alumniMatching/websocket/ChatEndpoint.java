package org.wxl.alumniMatching.websocket;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.config.GetHttpSessionConfig;
import org.wxl.alumniMatching.contant.MessageConstant;
import org.wxl.alumniMatching.contant.UserConstant;
import org.wxl.alumniMatching.domain.dto.SendMessageDTO;
import org.wxl.alumniMatching.domain.entity.MessageUser;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageUserService;
import org.wxl.alumniMatching.utils.MessageUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public static final Map<Long,Session> ONLINE_USERS = new ConcurrentHashMap<>();

    /**
     * HttpSession对象中存储了当前登录用户的信息
     */
    private HttpSession httpSession;

    private static IMessageUserService messageUserService;

    @Autowired
    public void setMessageUserService(IMessageUserService messageUserService){
        ChatEndpoint.messageUserService = messageUserService;
    }

    private static RedisTemplate redisTemplate;
    @Autowired
    private void setRedisTemplate(RedisTemplate redisTemplate){
        ChatEndpoint.redisTemplate = redisTemplate;
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

        //当前在线列表
        for (Map.Entry<Long, Session> entry : ONLINE_USERS.entrySet()) {
            System.out.println("----------------"+entry.getKey() + " "  + entry.getValue());
        }
    }


    /**
     *浏览器发送消息到服务端，该方法被调用
     *
     * @param message {"sendUserId":"1","receiveUserId":"2","content":"你好"}
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

            if (messageDTO.getSendUserId() == null || messageDTO.getSendUserId() <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择发送人");
            }
            if (messageDTO.getReceiveUserId() == null || messageDTO.getReceiveUserId() <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择接收人");
            }
            if (StringUtils.isBlank(messageDTO.getContent())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入内容");
            }

            //获取要将数据发送的用户
            Long toUserId = messageDTO.getReceiveUserId();
            //获取消息数据
            String data = messageDTO.getContent();
            //获取当前登录用户
            User user = (User)httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            //发送时间
            LocalDateTime now = LocalDateTime.now();
            //获取推送给指定用户的消息格式的数据
            String resultMessage = MessageUtils.getMessage(MessageConstant.NOT_SYSTEM_MESSAGE,user.getId(), toUserId, data,now);
            MessageUser messageUser = new MessageUser();
            messageUser.setSendUserId(user.getId());
            messageUser.setContent(data);
            messageUser.setSendTime(now);
            messageUser.setReceiveUserId(toUserId);
            messageUser.setIsSystem(0);
            messageUser.setStatus(0);

            //如果发送消息对象在线
            if (ONLINE_USERS.get(toUserId) != null){
                ONLINE_USERS.get(toUserId).getBasicRemote().sendText(resultMessage);
            }

                //用Redis的Set方法存入当前哪些用户给某一位用户发送了消息
                //当key用户与存入value中的用户聊天时，就删除value中的某一用户
                String sendMessageUserKey = String.format("alumniMatching:message:sendMessageUser:%s",toUserId.toString());
                redisTemplate.opsForSet().add(sendMessageUserKey,user.getId());


                //不管用户在没在线，都存入redis中
                String readKey = String.format("alumniMatching:message:onMessage:%s", user.getId().toString());
                //将消息存入redis中
                addRedis(readKey,toUserId,messageUser);
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
        //将消息记录存入数据库中
        saveMessageUser(user);
        System.out.println(user.getUsername() + "退出聊天室------------------");
    }


    /**
     * 发送错误时，该方法被调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        User user = (User)this.httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
        saveMessageUser(user);
        log.error("用户名：" +user.getUsername() + "————发送消息出错："+error);
        error.printStackTrace();
    }


    /**
     * 将已读或未读的消息存入redis中
     *
     * @param redisKey redis的key
     * @param toUserId 接受消息的用户
     * @param messageUser 消息记录
     */
    private void addRedis(String redisKey,Long toUserId,MessageUser messageUser){
        List<MessageUser> messageList;
        if (redisTemplate.opsForHash().hasKey(redisKey, toUserId.toString())) {
            messageList = (List<MessageUser>) redisTemplate.opsForHash().get(redisKey, toUserId.toString());
            if (messageList == null || messageList.size() == 0){
                messageList = new ArrayList<>();
            }
        } else {
            messageList = new ArrayList<>();
        }
        messageList.add(messageUser);
        //存入redis中
        redisTemplate.opsForHash().put(redisKey,toUserId.toString(),messageList);
    }


    /**
     * 保存当前退出用户的消息记录
     * @param user 当前退出用户
     */
    private void saveMessageUser(User user){
        String readKey = String.format("alumniMatching:message:onMessage:%s", user.getId().toString());
        //如果redis中没有存入该用户的消息记录，则直接返回
        if (redisTemplate.opsForHash().size(readKey) == 0){
            return;
        }else {
            //如果redis中存入了该用户的消息记录，则出去存入数据库中
            Set keys = redisTemplate.opsForHash().keys(readKey);
            keys.stream().forEach(key->{
                List<MessageUser> messageVOS = (List<MessageUser>) redisTemplate.opsForHash().get(readKey, key);
                boolean b = messageUserService.saveBatch(messageVOS);
                if (b){
                    //请空redis数据
                    redisTemplate.opsForHash().delete(readKey,key);
                }else {
                    log.error("userId为：" + user.getId() + "将与用户id为:"+ key +" 的redis消息记录存入数据库失败！！！！！！！！！！！");
                }
            });
        }
    }
}
