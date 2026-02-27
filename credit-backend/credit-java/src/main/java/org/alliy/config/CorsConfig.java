package org.alliy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 1. 允许所有的请求域名访问
        config.addAllowedOriginPattern("*");
        // 2. 允许所有的请求头
        config.addAllowedHeader("*");
        // 3. 允许所有的请求方式：GET, POST, PUT, DELETE...
        config.addAllowedMethod("*");
        // 4. 允许带上认证信息（Cookie等）
        config.setAllowCredentials(true);
        // 5. 设置浏览器可以缓存跨域信息的时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有的路径生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}