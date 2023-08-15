package org.wxl.alumniMatching.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.PageVO;
import org.wxl.alumniMatching.domain.vo.UserTagVO;
import org.wxl.alumniMatching.service.IUserService;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 首页缓存预热任务
 *
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private IUserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private  List<Long> mainUserList = Collections.singletonList(1L);

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 30 * * * ? ")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("alumniMatching:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                    //排除自己的信息
                    queryWrapper.ne(User::getId,userId);
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    List<UserTagVO> userTagVOS = BeanCopyUtils.copyBeanList(userPage.getRecords(), UserTagVO.class);
                    PageVO pageVO = new PageVO(userTagVOS,userPage.getTotal());
                    String redisKey = String.format("alumniMatching:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, pageVO, 1000 * 60 * 60, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
