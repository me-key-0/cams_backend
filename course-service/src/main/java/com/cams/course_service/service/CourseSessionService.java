package com.cams.course_service.service;


import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.cams.course_service.dto.CourseSessionResponse;
import com.cams.course_service.model.CourseSession;



public interface CourseSessionService {
    CourseSession getCourseSession(@PathVariable Long id);

    List<CourseSession> getCourseSessionsByDepartment(Long departmentId);

    // List<CourseSessionDto> getCourseSessionsByStudent(Long studentId);
} 
