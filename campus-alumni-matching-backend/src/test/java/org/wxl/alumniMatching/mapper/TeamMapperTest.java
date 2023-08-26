package org.wxl.alumniMatching.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class TeamMapperTest {
    @Resource
    private TeamMapper teamMapper;

    @Test
    public void selectMatchTeamsTest(){
        //只查找队伍的主键，最大队伍人数要大于2，队伍要没有满员，队伍状态不能是私密的，队伍没有过期
//        List<Long> list = teamMapper.selectMatchTeams();
//        list.forEach(System.out::println);
    }
}
