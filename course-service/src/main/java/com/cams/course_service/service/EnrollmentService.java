package com.cams.course_service.service;

import java.util.List;

import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.model.CourseSession;

public interface EnrollmentService {
    List<CourseSessionDto> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear);
    List<CourseSessionDto> getCourseSessionsByStudent(Long studentId);
} 