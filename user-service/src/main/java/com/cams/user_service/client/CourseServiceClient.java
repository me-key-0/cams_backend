package com.cams.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseServiceClient {
    
    @GetMapping("/api/session/{id}/exists")
    boolean checkCourseSessionExists(@PathVariable("id") Long courseSessionId);
    
    @GetMapping("/api/enrollment/check-enrollment")
    boolean isStudentEnrolled(@PathVariable("studentId") Long studentId, 
                             @PathVariable("courseSessionId") Long courseSessionId);
    
    @GetMapping("/api/assignment/lecturer/{lecturerId}/validate/{courseSessionId}")
    boolean validateLecturerForCourseSession(@PathVariable("lecturerId") Long lecturerId, 
                                           @PathVariable("courseSessionId") Long courseSessionId);
}