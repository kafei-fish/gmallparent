package com.lxl.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/20 16:40
 * @PackageName:com.lxl.gmall.item.config
 * @ClassName: ThreadPoolConfig
 * @Description: TODO
 * @Version 1.0
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
            12,
            120,
            3,
            TimeUnit.SECONDS,
             new ArrayBlockingQueue<>(3)
        );

    }
}
