package com.cams.user_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cams.user_service.model.Lecturer;

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

    @GetMapping("/api/session/{courseSessionId}/lecturers")
    List<Lecturer> getLecturerIdsByCourseSessionId(@PathVariable("courseSessionId") Long courseSessionId);
}
