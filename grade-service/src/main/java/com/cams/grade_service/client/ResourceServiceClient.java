package com.cams.grade_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    
    @PostMapping(value = "/api/v1/resources", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResourceDto uploadResource(@RequestPart("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("type") String type,
                              @RequestParam("courseSessionId") Long courseSessionId,
                              @RequestParam("categories") List<String> categories,
                              @RequestHeader("X-User-Id") String userId,
                              @RequestHeader("X-User-Role") String role);
}