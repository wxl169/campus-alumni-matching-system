package org.wxl.alumniMatching.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wxl.alumniMatching.domain.vo.UserTagVO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@SpringBootTest
public class IUserServiceTest {
@Autowired
private IUserService userService;

    @Test
    public void testSearchUserByTags(){
//        List<String> tageNameList = Arrays.asList("Python");
//        List<UserTagVO> userTagVOS = userService.searchUserByTags(tageNameList);
//        userTagVOS.forEach(System.out::println);
    }

}