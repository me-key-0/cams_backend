package com.cams.resource_service.config;

import com.cams.resource_service.exception.CourseSessionNotFoundException;
import com.cams.resource_service.exception.UnauthorizedAccessException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                switch (response.status()) {
                    case 404:
                        return new CourseSessionNotFoundException("Course session not found");
                    case 403:
                        return new UnauthorizedAccessException("Unauthorized access to course session");
                    default:
                        return new RuntimeException("Error calling course service");
                }
            }
        };
    }
} 