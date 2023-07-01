package com.imooc.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    /**
     * 返回一个RedissonClient对象，
     * 自动装载到Spring的Bean工厂中去
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 使用单机模式
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0);

        return Redisson.create(config);
    }


}
