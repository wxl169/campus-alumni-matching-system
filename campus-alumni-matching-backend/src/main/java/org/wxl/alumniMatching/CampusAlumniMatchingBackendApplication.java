package org.wxl.alumniMatching;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 16956
 */
@SpringBootApplication
@MapperScan("org.wxl.alumniMatching.mapper")
//开启对定时任务的支持
@EnableScheduling
public class CampusAlumniMatchingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusAlumniMatchingBackendApplication.class, args);
    }

}
