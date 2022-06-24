package com.lxl.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/15 13:22
 * @PackageName:com.lxl.gmall.item
 * @ClassName: ServiceItemApplication
 * @Description: TODO
 * @Version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)

@EnableDiscoveryClient
@ComponentScan(basePackages = "com.lxl")
@EnableFeignClients(basePackages = {"com.lxl"})
@EnableSwagger2
public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class,args);
    }
}
