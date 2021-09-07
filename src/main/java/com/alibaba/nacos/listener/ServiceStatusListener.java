package com.alibaba.nacos.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.util.HashRingUtil;
import com.alibaba.nacos.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @description: 服务监听器
 * @author: fengandong
 * @time: 2020/12/31 23:40
 */
@Component
public class ServiceStatusListener implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(ServiceStatusListener.class);
    private final String SERVICE_NAME = "ufire-websocket";
    //初始化监听服务上下线
    @Override
    public void run(String... args) {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", "127.0.0.1:8848");
            properties.setProperty("namespace", "public");
            NamingService namingService = NamingFactory.createNamingService(properties);
            RedisTemplate redisTemplate = (RedisTemplate) SpringUtil.getBean("redisTemplate");
            namingService.subscribe(SERVICE_NAME, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    List<Instance> instances = ((NamingEvent) event).getInstances();
                    Boolean delete = redisTemplate.delete(SERVICE_NAME);
                    instances.stream().forEach(instance -> {
                        String host = instance.getIp() + ":" + instance.getPort();
                        Integer hash = HashRingUtil.getHash(host);
                        redisTemplate.opsForHash().put(SERVICE_NAME, hash.toString(), host);
                    });
                    redisTemplate.convertAndSend(SERVICE_NAME, JSON.toJSONString(instances));
                    System.out.println("监听到服务:" + SERVICE_NAME + " 发生变动" + JSON.toJSONString(instances));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
