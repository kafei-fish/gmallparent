package com.lxl.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

/**
 * author:atGuiGu-mqx
 * date:2022/6/11 14:29
 * 描述：
 **/
@Configuration
public class CorsConfig {

    //  将WebFilter 注入到spring 容器中
    @Bean
    public WebFilter webFilter(){
        //  cors 响应：
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true); // 可以携带cookie
        corsConfiguration.addAllowedOrigin("*"); // 允许跨域
        corsConfiguration.addAllowedMethod("*"); // 设置请求的方法 GET POST ...
        corsConfiguration.addAllowedHeader("*"); // 允许携带请求头
        //  创建对象
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //  经过网关的所有请求都实现跨域
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        //  返回对象
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
