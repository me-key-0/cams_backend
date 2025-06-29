package com.cams.user_service.service;

import com.cams.user_service.dto.*;
import com.cams.user_service.model.EvaluationQuestion;

import java.util.List;

public interface EvaluationService {
    // Session management
    ConfirmationDto activateEvalSession(Long sessionId);
    ConfirmationDto isEvalSessionActivated(Long sessionId);
    EvaluationSessionDto createEvaluationSession(CreateEvaluationSessionRequest request, Long adminId);
    List<EvaluationSessionDto> getEvaluationSessionsByDepartment(Long departmentId);
    
    // Evaluation submission
    ConfirmationDto submitEvaluation(Long studentId, EvaluationRequestDto request);
    
    // Questions and categories
    List<EvaluationQuestion> getEvaluationQuestions();
    List<EvaluationQuestionDto> getEvaluationQuestionsByCategory(Long categoryId);
    List<EvaluationCategoryDto> getEvaluationCategories();
    
    // Analytics
    EvaluationAnalyticsResponse getEvaluationAnalytics(Long courseSessionId, Long lecturerId);
    List<EvaluationAnalyticsResponse> getLecturerEvaluationAnalytics(Long lecturerId);
    List<EvaluationAnalyticsResponse> getDepartmentEvaluationAnalytics(Long departmentId);
}