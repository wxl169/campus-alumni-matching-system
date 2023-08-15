package org.wxl.alumniMatching;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wxl.alumniMatching.domain.entity.Team;

@SpringBootTest
public class FirstTest {
    @Test
    public void test(){
        Team team = new Team();
        Long id = team.getId();
    }
}
