package org.wxl.alumniMatching.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wxl.alumniMatching.domain.entity.User;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class UserMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void  getMatchUsersTest(){
//        List<User> matchUsers = userMapper.getMatchUsers(1L);
//        matchUsers.forEach(System.out::println);
    }
}
