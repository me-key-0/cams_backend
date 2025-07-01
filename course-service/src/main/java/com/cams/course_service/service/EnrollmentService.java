package com.cams.course_service.service;

import java.util.List;

import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.model.Enrollment;

public interface EnrollmentService {
    List<CourseSessionDto> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear);
    List<CourseSessionDto> getCourseSessionsByStudent(Long studentId);
    
    // New methods
    Enrollment enrollStudent(Long studentId, Long courseSessionId);
    void unenrollStudent(Long studentId, Long courseSessionId);
    boolean isStudentEnrolled(Long studentId, Long courseSessionId);
    List<Long> getEnrolledStudents(Long courseSessionId);
}