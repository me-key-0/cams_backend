package com.cams.course_service.service;

import com.cams.course_service.dto.*;

import java.util.List;

public interface BatchService {
    
    // Batch Management
    BatchResponse createBatch(BatchRequest request, Long adminId);
    BatchResponse updateBatch(Long id, BatchRequest request, Long adminId);
    void deleteBatch(Long id, Long adminId);
    BatchResponse getBatchById(Long id);
    List<BatchResponse> getBatchesByDepartment(Long departmentId);
    
    // Course Assignment
    List<CourseAssignmentResponse> assignCoursesToBatch(CourseAssignmentRequest request, Long adminId);
    List<CourseAssignmentResponse> getCourseAssignmentsForBatch(Long batchId);
    void removeCourseAssignment(Long assignmentId, Long adminId);
    
    // Batch Progression
    BatchResponse advanceBatchSemester(Long batchId, Long adminId);
    
    // Validation
    boolean validateCreditHours(Long batchId, Integer year, Integer semester, List<Long> courseIds);
}