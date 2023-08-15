package org.wxl.alumniMatching.once;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IUserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class InsertUserTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private IUserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 插入用户数据
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    @Test
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
            user.setSchool("重庆工程学院");
            user.setProfile("个人介绍"+i);
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 通过将数据添加到集合中，然后分批次导入数据
     */
    @Test
    public  void insertUserList(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM =100000;
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < INSERT_NUM; i++){
            User user = new User();
            user.setUsername("用户"+i);
            user.setUserAccount("用户账户"+i);
            user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.1&pid=3.1&rm=2");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("18184051000");
            user.setEmail("1695600000@qq.com");
            user.setSchool("重庆工程学院");
            user.setProfile("个人介绍"+i);
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 10000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("用户"+i);
                user.setUserAccount("用户账户"+i);
                user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.1&pid=3.1&rm=2");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("18184051000");
                user.setEmail("1695600000@qq.com");
                user.setSchool("重庆工程学院");
                user.setProfile("个人介绍"+i);
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 执行CompletableFuture.runAsync，里面的操作是异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            //拿到10个异步任务
            futureList.add(future);
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}

