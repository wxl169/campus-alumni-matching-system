package org.wxl.alumniMatching.once;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.mapper.UserMapper;

import javax.annotation.Resource;

@Component
public class InsertUser {
    @Resource
    private UserMapper userMapper;

    /**
     * 插入用户数据
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public  void insertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM =1000;
        for(int i = 0; i < INSERT_NUM; i++){
            User user = new User();
            user.setUsername("用户"+i);
            user.setUserAccount("用户账户"+i);
            user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.1&pid=3.1&rm=2");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("18184051000");
            user.setEmail("1695600000@qq.com");
            user.setProfile("个人介绍"+i);
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}

