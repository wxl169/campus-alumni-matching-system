package org.wxl.alumniMatching.config;

import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.exception.BusinessException;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author 16956
 */
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //获取HttpSession对象
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        //将httpSession对象保存起来
        if (httpSession == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请登录账号");
        }
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}
