package com.cams.grade_service.service;

import com.cams.grade_service.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GradingService {
    
    // Grade Type Management
    GradeTypeResponse createGradeType(GradeTypeRequest request, Long lecturerId, String lecturerName);
    GradeTypeResponse updateGradeType(Long id, GradeTypeRequest request, Long lecturerId);
    void deleteGradeType(Long id, Long lecturerId);
    List<GradeTypeResponse> getGradeTypesByCourseSession(Long courseSessionId);
    GradeTypeResponse getGradeTypeById(Long id);
    
    // Student Grade Management
    StudentGradeResponse addOrUpdateGrade(StudentGradeRequest request, Long lecturerId, String lecturerName);
    List<StudentGradeResponse> addBulkGrades(BulkGradeRequest request, Long lecturerId, String lecturerName);
    void deleteGrade(Long studentId, Long gradeTypeId, Long lecturerId);
    
    // Gradebook Management
    GradebookResponse getGradebook(Long courseSessionId, Long lecturerId);
    byte[] exportGradebookToExcel(Long courseSessionId, Long lecturerId);
    List<StudentGradeResponse> importGradesFromExcel(MultipartFile file, Long courseSessionId, Long gradeTypeId, Long lecturerId, String lecturerName);
    
    // Student Assessment Overview
    StudentAssessmentOverviewResponse getStudentAssessmentOverview(Long studentId, Long courseSessionId);
    List<AssessmentItemResponse> getStudentAssessments(Long studentId, Long courseSessionId);
    
    // Default Grade Types
    void createDefaultGradeTypes(Long courseSessionId, Long lecturerId, String lecturerName);
}