package com.cams.user_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "course-service")
public interface CourseServiceClient {
    
    @GetMapping("/api/session/{id}/exists")
    boolean checkCourseSessionExists(@PathVariable("id") Long courseSessionId);
    
    @GetMapping("/api/enrollment/check-enrollment")
    boolean isStudentEnrolled(@RequestParam("studentId") Long studentId, 
                             @RequestParam("courseSessionId") Long courseSessionId);
    
    @GetMapping("/api/assignment/lecturer/{lecturerId}/validate/{courseSessionId}")
    boolean validateLecturerForCourseSession(@PathVariable("lecturerId") Long lecturerId, 
                                           @PathVariable("courseSessionId") Long courseSessionId);
    
    @GetMapping("/api/session/{courseSessionId}/lecturers")
    List<Long> getLecturerIdsByCourseSessionId(@PathVariable("courseSessionId") Long courseSessionId);
}
