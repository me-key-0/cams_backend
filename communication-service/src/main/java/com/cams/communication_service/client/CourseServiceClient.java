package com.cams.communication_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "course-service")
public interface CourseServiceClient {
    
    @GetMapping("/api/enrollment/student/{studentId}")
    List<CourseSessionDto> getCourseSessionByStudentId(@PathVariable("studentId") Long studentId);
    
    @GetMapping("/api/assignment/lecturer/{lecturerId}/validate/{courseSessionId}")
    boolean validateLecturerForCourseSession(@PathVariable("lecturerId") Long lecturerId, 
                                           @PathVariable("courseSessionId") Long courseSessionId);
}