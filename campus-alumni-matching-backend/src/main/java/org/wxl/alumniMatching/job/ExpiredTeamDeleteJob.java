package org.wxl.alumniMatching.job;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wxl.alumniMatching.service.ITeamService;


import javax.annotation.Resource;


/**
 * 过期队伍删除
 * @author 16956
 */
@Component
@Slf4j
public class ExpiredTeamDeleteJob {
    @Resource
    private ITeamService teamService;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void doCacheRecommendUser() {
        //每天检查队伍是否要过期，提前三天提醒群聊
    }

}
