package com.cams.auth_service.config;

import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                switch (response.status()) {
                    case 403:
                        return new BadCredentialsException("Invalid credentials");
                    case 404:
                        return new BadCredentialsException("User not found");
                    default:
                        return new RuntimeException("Error calling user service");
                }
            }
        };
    }
} 