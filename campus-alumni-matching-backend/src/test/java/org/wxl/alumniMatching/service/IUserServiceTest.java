package org.wxl.alumniMatching.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.wxl.alumniMatching.domain.vo.UserTagVO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class IUserServiceTest {
@Autowired
private IUserService userService;
@Autowired
private RedisTemplate redisTemplate;

    @Test
    public void testSearchUserByTags(){
//        List<String> tageNameList = Arrays.asList("Python");
//        List<UserTagVO> userTagVOS = userService.searchUserByTags(tageNameList);
//        userTagVOS.forEach(System.out::println);
//        String sendMessageUserKey = String.format("alumniMatching:message:sendMessageUser:%s",3);
        //如果redis中没有存入则跳过
//        Set<Integer> ids = new HashSet<>();
//        if (redisTemplate.opsForSet().size(sendMessageUserKey) != 0){
//           ids = redisTemplate.opsForSet().members(sendMessageUserKey);
//        }
//        ids.forEach(System.out::println);
    }

    @Test
    public void testRead(){
//        String unReadKey = String.format("alumniMatching:message:onMessage:read:%s:%s", "1","2");
//        String pop = (String) redisTemplate.opsForList().leftPop(unReadKey);
//        System.out.println(pop);
    }

}