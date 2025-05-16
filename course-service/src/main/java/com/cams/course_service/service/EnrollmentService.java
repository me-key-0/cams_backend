package com.cams.course_service.service;

import java.util.List;

import com.cams.course_service.model.CourseSession;

public interface EnrollmentService {
    List<CourseSession> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear);
} 