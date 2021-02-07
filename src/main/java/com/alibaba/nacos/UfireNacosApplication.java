
package com.alibaba.nacos;
import com.alibaba.nacos.config.ConfigConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author nacos
 * <p>
 * nacos console 源码运行
 */
@EnableScheduling
@SpringBootApplication
public class UfireNacosApplication {

    public static void main(String[] args) {
        System.setProperty(ConfigConstants.STANDALONE_MODE, "true");
        System.setProperty(ConfigConstants.AUTH_ENABLED, "false");
        SpringApplication.run(UfireNacosApplication.class, args);
    }

}
