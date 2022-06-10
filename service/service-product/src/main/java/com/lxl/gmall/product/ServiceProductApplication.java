package com.lxl.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
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
@EnableSwagger2
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class,args);
    }
}
