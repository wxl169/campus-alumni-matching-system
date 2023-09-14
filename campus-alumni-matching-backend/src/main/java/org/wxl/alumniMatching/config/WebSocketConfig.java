package org.wxl.alumniMatching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 扫描所有添加@ServerEndpoint注解的类
 * @author 16956
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入ServerEndpointExporter,自动注册使用@ServerEndpoint注解的
     */
//    @Profile({"dev", "test"})
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
