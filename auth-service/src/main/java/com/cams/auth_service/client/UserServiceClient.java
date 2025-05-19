package com.cams.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/validate")
    boolean validateCredentials(@RequestParam("email") String email, @RequestParam("password") String password);
    
    @GetMapping("/api/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);
} 