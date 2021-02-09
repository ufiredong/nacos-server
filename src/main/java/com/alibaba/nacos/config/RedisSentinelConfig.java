package com.alibaba.nacos.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableAutoConfiguration
@Slf4j
public class RedisSentinelConfig {

    @Value("#{'${spring.redis.sentinel.nodes}'.split(',')}")
    private List<String> nodes;

    @Bean
    @ConfigurationProperties(prefix="spring.redis")
    public JedisPoolConfig getRedisConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }
    @Bean
    public RedisSentinelConfiguration sentinelConfiguration(){
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        //配置matser的名称
        redisSentinelConfiguration.master("mymaster");
        //配置redis的哨兵sentinel
        Set<RedisNode> redisNodeSet = new HashSet<>();
        nodes.forEach(x->{
            redisNodeSet.add(new RedisNode(x.split(":")[0],Integer.parseInt(x.split(":")[1])));
        });
        log.info("redisNodeSet -->"+redisNodeSet);
        //哨兵模式添加set
        redisSentinelConfiguration.setSentinels(redisNodeSet);
        return redisSentinelConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, RedisSentinelConfiguration sentinelConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig,jedisPoolConfig);
        return jedisConnectionFactory;
    }

}
