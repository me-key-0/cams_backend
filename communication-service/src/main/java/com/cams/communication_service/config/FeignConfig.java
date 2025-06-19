package com.cams.communication_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Clear any existing headers that might cause authentication issues
            requestTemplate.headers().clear();
        };
    }
}
