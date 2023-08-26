package org.wxl.alumniMatching.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class UserTeamMapperTest {
    @Resource
    private UserTeamMapper userTeamMapper;

    @Test
    public void getUserTags(){
//        List<String> userTags = userTeamMapper.getUserTags(1L);
//        userTags.forEach(System.out::println);
    }
}
