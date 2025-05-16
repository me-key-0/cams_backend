package com.cams.grade_service.service;

import com.cams.grade_service.dto.AssessmentSubmissionDto;

import java.util.List;

public interface AssessmentSubmissionService {
    AssessmentSubmissionDto submitAssessment(AssessmentSubmissionDto dto);
    AssessmentSubmissionDto updateSubmission(Long id, AssessmentSubmissionDto dto);
    void deleteSubmission(Long id);
    AssessmentSubmissionDto getSubmission(Long id);
    List<AssessmentSubmissionDto> getAllSubmissions();
    List<AssessmentSubmissionDto> getSubmissionsByStudent(Long studentId);
    List<AssessmentSubmissionDto> getSubmissionsByAssessment(Long assessmentId);
}