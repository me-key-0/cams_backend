package com.cams.grade_service.service;

import com.cams.grade_service.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService {
    
    // Assignment management with file upload
    AssignmentResponse createAssignmentWithFiles(AssignmentCreateRequest request, List<MultipartFile> files, 
                                               Long lecturerId, String lecturerName);
    AssignmentResponse createAssignment(AssignmentCreateRequest request, Long lecturerId, String lecturerName);
    AssignmentResponse updateAssignment(Long id, AssignmentCreateRequest request, Long lecturerId);
    void deleteAssignment(Long id, Long lecturerId);
    AssignmentResponse getAssignmentById(Long id, Long userId, String role);
    
    // Assignment listing
    List<AssignmentResponse> getAssignmentsByCourseSession(Long courseSessionId, Long userId, String role);
    List<AssignmentResponse> getMyAssignments(Long lecturerId);
    List<StudentAssignmentResponse> getStudentAssignments(Long studentId);
    
    // Assignment status management
    AssignmentResponse publishAssignment(Long id, Long lecturerId);
    AssignmentResponse closeAssignment(Long id, Long lecturerId);
    
    // Submission management with file upload
    SubmissionResponse submitAssignmentWithFiles(SubmissionCreateRequest request, List<MultipartFile> files,
                                               Long studentId, String studentName);
    SubmissionResponse submitAssignment(SubmissionCreateRequest request, Long studentId, String studentName);
    SubmissionResponse updateSubmission(Long submissionId, SubmissionCreateRequest request, Long studentId);
    void deleteSubmission(Long submissionId, Long studentId);
    
    // Grading
    SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request, Long lecturerId, String lecturerName);
    List<SubmissionResponse> getAssignmentSubmissions(Long assignmentId, Long lecturerId);
    SubmissionResponse getSubmissionById(Long submissionId, Long userId, String role);
    
    // Student submissions
    List<SubmissionResponse> getMySubmissions(Long studentId);
    SubmissionResponse getMySubmissionForAssignment(Long assignmentId, Long studentId);
}