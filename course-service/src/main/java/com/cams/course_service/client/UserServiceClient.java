package com.cams.course_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cams.course_service.dto.LecturerDto;

@FeignClient(name = "user-service", path = "/api/user/lecturer")
public interface UserServiceClient {
    
    @GetMapping("/{lecturerId}")
    LecturerDto getLecturerById(@PathVariable("lecturerId") Long lecturerId);
} 