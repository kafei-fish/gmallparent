package com.lxl.gmall.product;

import com.lxl.gmall.common.constant.RedisConst;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 14:54
 * @PackageName:com.lxl.gmall.product
 * @ClassName: ServiceProductApplication
 * @Description: TODO
 * @Version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.lxl")
@EnableSwagger2
public class ServiceProductApplication implements CommandLineRunner {
    @Autowired
    private RedissonClient redissonClient;
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        // 初始化布隆过滤器，预计统计元素数量为100000，期望误差率为0.01
        bloomFilter.tryInit(10000,0.01);
    }
}
