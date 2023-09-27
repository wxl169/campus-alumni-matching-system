package org.wxl.alumniMatching.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 第一种跨域配置
 * @author 16956
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//            //设置允许跨域的路径
//            registry.addMapping("/**")
//                    //设置允许跨域请求的域名
//                    //当**Credentials为true时，**Origin不能为星号，需为具体的ip地址【如果接口不带cookie,ip无需设成具体ip】
//                    .allowedOrigins("http://127.0.0.1:5173","http://localhost:5173","http://localhost:8000"
//                            ,"http://42.193.15.245:8081","http://127.0.0.1:8081","http://localhost:8081",
//                            "http://42.193.15.245:80","http://42.193.15.245:81","http://localhost:80","http://localhost:81")
//                    //是否允许证书 不再默认开启
//                    .allowCredentials(true)
//                    .allowedHeaders(CorsConfiguration.ALL)
//                    .allowedMethods(CorsConfiguration.ALL)
//                    //设置允许的方法
//                    .allowedMethods("*")
//                    //跨域允许时间
//                    .maxAge(3600);
//
//    }


}
