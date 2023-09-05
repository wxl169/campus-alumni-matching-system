package org.wxl.alumniMatching.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson实现分布式锁
 *
         */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;
    private int redisson_database;
    @Bean
    public RedissonClient redissonClient(){
        //1.创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(redisson_database);
        System.out.println("redisson_database------------> " + redisson_database);
        //创建实例
        return Redisson.create(config);
    }

}
