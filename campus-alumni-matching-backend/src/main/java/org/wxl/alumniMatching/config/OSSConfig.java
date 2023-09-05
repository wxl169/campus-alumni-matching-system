package org.wxl.alumniMatching.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 16956
 */
@Component
@PropertySource("classpath:oss.properties")
@ConfigurationProperties(prefix = "oss")
@Data
public class OSSConfig {
    private String accessKey;

    private String secretKey;
    private String bucket;
    private String cdnTest;
}
