package com.cams.course_service.service;

import com.cams.course_service.dto.*;

import java.util.List;

public interface CourseSessionManagementService {
    
    // Course Session Management
    CourseSessionResponse createCourseSession(CourseSessionRequest request, Long adminId);
    CourseSessionResponse updateCourseSession(Long id, CourseSessionRequest request, Long adminId);
    void deleteCourseSession(Long id, Long adminId);
    CourseSessionResponse getCourseSessionById(Long id);
    List<CourseSessionResponse> getCourseSessionsByDepartment(Long departmentId);
    
    // New methods for batch-specific course sessions
    List<CourseSessionResponse> getCourseSessionsByBatch(Long batchId);
    List<CourseSessionResponse> getCourseSessionsByBatchAndSemester(Long batchId, Integer year, Integer semester);
    
    // Session Activation
    CourseSessionResponse activateCourseSession(Long id, Long adminId);
    CourseSessionResponse deactivateCourseSession(Long id, Long adminId);
    
    // Enrollment Management
    CourseSessionResponse openEnrollment(Long id, Long adminId);
    CourseSessionResponse closeEnrollment(Long id, Long adminId);
    
    // Lecturer Assignment
    CourseSessionResponse assignLecturers(Long id, List<Long> lecturerIds, Long adminId);
    CourseSessionResponse removeLecturer(Long id, Long lecturerId, Long adminId);
    
    // Validation
    boolean validateLecturerCapacity(Long lecturerId, Long courseId);
}