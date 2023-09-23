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
import org.wxl.alumniMatching.domain.dto.SendTeamMessageDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IMessageTeamService;
import org.wxl.alumniMatching.utils.TeamMessageUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 16956
 */
@ServerEndpoint(value = "/messageTeam",configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class ChatEndpointTeam {
    /**
     * 当前在线的群成员 用户id，session
     */
    private static final Map<Long, Session> ONLINE_USERS_TEAM = new ConcurrentHashMap<>();

    /**
     * HttpSession对象中存储了当前登录用户的信息
     */
    private HttpSession httpSession;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private static IMessageTeamService messageTeamService;

    @Autowired
    public void setMessageUserService(IMessageTeamService messageTeamService){
        ChatEndpointTeam.messageTeamService = messageTeamService;
    }

    /**
     * 获取进入群聊的用户信息
     * @param config  配置信息
     * @return 当前用户信息
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
        ONLINE_USERS_TEAM.put(user.getId(),session);

        //是否有暂存的消息，如果有则发送消息

        //当前在线列表
        for (Map.Entry<Long, Session> entry : ONLINE_USERS_TEAM.entrySet()) {
            System.out.println("进入聊天室----------------"+entry.getKey() + " "  + entry.getValue());
        }
    }


    /**
     * 广播消息
     * @param message 发送的消息
     */
    private void broadcastAllUsers(User user,String message){
        //遍历map集合
        try {
            //当前登录用户的id
            Long userId = user.getId();
            //小组内所有在线的用户
            Set<Map.Entry<Long, Session>> entrySet = ONLINE_USERS_TEAM.entrySet();
            //存入当前登录的用户id
            Set<Long> loginUserId = new HashSet<>(16);
            entrySet = entrySet.stream().filter(teamMessage ->{
                //将所有在线的用户id加入set集合
                loginUserId.add(teamMessage.getKey());
                //排除当前登录用户
                if (teamMessage.getKey().equals(userId)){
                    return false;
                }
                return true;
            }).collect(Collectors.toSet());
            if (entrySet.size() > 0){
                for (Map.Entry<Long,Session> entry : entrySet) {
                    //获取到所有用户对应的session对象
                    Session session = entry.getValue();
                    //发送消息
                    session.getBasicRemote().sendText(message);
                }
            }
            //将消息保存在数据库
            Gson gson = new Gson();
            TeamMessageVO teamMessageVO = gson.fromJson(message, TeamMessageVO.class);
            boolean judge = messageTeamService.saveTeamMessage(loginUserId,teamMessageVO);
            if (!judge){
                log.error("队伍id为："+teamMessageVO.getTeamId()+"------------》消息保持失败");
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
            //将消息推送给指定的用户
            Gson gson = new Gson();
            SendTeamMessageDTO sendTeamMessageDTO = gson.fromJson(message, SendTeamMessageDTO.class);

            if (sendTeamMessageDTO.getTeamId() == null || sendTeamMessageDTO.getTeamId() <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择发送群");
            }
            if (StringUtils.isBlank(sendTeamMessageDTO.getContent())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入内容");
            }

            //获取发送消息的群聊id
            Long teamId = sendTeamMessageDTO.getTeamId();
            //获取消息数据
            String data = sendTeamMessageDTO.getContent();
            //获取当前登录用户
            User user = (User)httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            //获取推送给指定用户的消息格式的数据
            String resultMessage = TeamMessageUtils.getMessage(MessageConstant.NOT_SYSTEM_MESSAGE, user, teamId, data);
            //群发消息
            this.broadcastAllUsers(user,resultMessage);
    }



    /**
     * 断开websocket连接时被调用
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session){
        User user = (User)this.httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
        ONLINE_USERS_TEAM.remove(user.getId());
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
        log.error("用户名：" +user.getUsername() + "————发送消息出错："+error);
        error.printStackTrace();
    }
}
