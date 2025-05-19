package com.cams.course_service.service;


import org.springframework.web.bind.annotation.PathVariable;
import com.cams.course_service.model.CourseSession;



public interface CourseSessionService {
    CourseSession getCourseSession(@PathVariable Long id);

    // List<CourseSessionDto> getCourseSessionsByStudent(Long studentId);
} 
