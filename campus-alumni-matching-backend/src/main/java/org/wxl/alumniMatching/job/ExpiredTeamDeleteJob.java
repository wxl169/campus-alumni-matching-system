package org.wxl.alumniMatching.job;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wxl.alumniMatching.contant.TeamConstant;
import org.wxl.alumniMatching.domain.entity.Team;
import org.wxl.alumniMatching.mapper.TeamMapper;
import org.wxl.alumniMatching.service.ITeamService;


import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


/**
 * TODO
 * 过期队伍删除
 * @author 16956
 */
@Component
@Slf4j
public class ExpiredTeamDeleteJob {
    @Resource
    private ITeamService teamService;
    @Resource
    private TeamMapper teamMapper;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void doCacheRecommendUser() {
        //每天检查队伍是否要过期，提前三天提醒群聊
        //1.查询所有设置过期时间的队伍
        List<Team> teamList =  teamMapper.selectHavingExpireTime();

        //过期队伍id集合
        Set<Long> teamIdSet = new HashSet<>();
        //预过期队伍
        Set<Long> preExpirationTeamId = new HashSet<>();

        for (Team team: teamList) {
            //2.判断过期时间是否还有0 ~ 7天
            Duration duration = Duration.between(LocalDateTime.now(), team.getExpireTime());
            if (duration.toDays() >= TeamConstant.TEAM_MIN_EXPIRED_TIME && duration.toDays() <= TeamConstant.TEAM_MAX_EXPIRED_TIME){
                //过期时间还有三天，则提醒队长和组员更新时间
                preExpirationTeamId.add(team.getId());
            }
            if (duration.toDays() < TeamConstant.TEAM_MIN_EXPIRED_TIME){
                //4.添加过期队伍id
                teamIdSet.add(team.getId());
            }
        }

        //预过期队伍提醒成员调整过期时间
        if (preExpirationTeamId.size() > 0){
            System.out.println("提醒群成员队伍过期");
        }
        //删除过期队伍
        if (teamIdSet.size() > 0){
            //删除队伍
            boolean judge = teamService.deleteTeamByList(teamIdSet);
            if (!judge){
                log.error("过期队伍删除出错");
            }
        }
    }

}
