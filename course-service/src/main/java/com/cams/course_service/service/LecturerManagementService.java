package com.cams.course_service.service;

import com.cams.course_service.dto.*;

import java.util.List;

public interface LecturerManagementService {

    
    
    // Lecturer Capacity Management
    LecturerCapacityResponse setLecturerCapacity(LecturerCapacityRequest request, Long adminId);
    LecturerCapacityResponse getLecturerCapacity(Long lecturerId);
    List<LecturerCapacityResponse> getLecturerCapacitiesByDepartment(Long departmentId);
    
    // Teachable Courses Management
    List<CourseDto> assignTeachableCourses(LecturerTeachableCoursesRequest request, Long adminId);
    List<CourseDto> getTeachableCourses(Long lecturerId);
    void removeTeachableCourse(Long lecturerId, Long courseId, Long adminId);
    
    // Validation
    boolean validateLecturerForCourse(Long lecturerId, Long courseId);
    boolean validateLecturerDepartment(Long lecturerId, Long departmentId);
}