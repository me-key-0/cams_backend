package com.cams.communication_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cams.communication_service.config.FeignConfig;

@FeignClient(name = "user-service", path = "/api/users/lecturer",configuration = FeignConfig.class)
public interface UserServiceClient {
    
    @GetMapping("/user_id/{userId}")
    Long getLecturerIdByUserId(@PathVariable("userId") Long userId);
}