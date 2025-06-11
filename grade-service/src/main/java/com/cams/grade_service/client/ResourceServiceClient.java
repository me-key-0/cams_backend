package com.cams.grade_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "resource-service")
public interface ResourceServiceClient {
    
    @GetMapping("/api/v1/resources/{id}")
    ResourceDto getResourceById(@PathVariable("id") Long resourceId,
                               @RequestHeader("X-User-Id") String userId,
                               @RequestHeader("X-User-Role") String role);
    
    @GetMapping("/api/v1/resources/course-session/{courseSessionId}")
    List<ResourceDto> getResourcesByCourseSession(@PathVariable("courseSessionId") Long courseSessionId,
                                                 @RequestHeader("X-User-Role") String role);
}