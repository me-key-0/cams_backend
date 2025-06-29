package com.cams.user_service.controller;

import com.cams.user_service.dto.*;
import com.cams.user_service.model.EvaluationQuestion;
import com.cams.user_service.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // Session management endpoints
    @PostMapping("/session")
    public ResponseEntity<EvaluationSessionDto> createEvaluationSession(
            @RequestBody CreateEvaluationSessionRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        EvaluationSessionDto session = evaluationService.createEvaluationSession(
            request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PostMapping("/session/{sessionId}/activate")
    public ResponseEntity<ConfirmationDto> activateSession(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ConfirmationDto result = evaluationService.activateEvalSession(sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/session/{sessionId}/status")
    public ResponseEntity<ConfirmationDto> getSessionStatus(@PathVariable Long sessionId) {
        ConfirmationDto result = evaluationService.isEvalSessionActivated(sessionId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sessions/department/{departmentId}")
    public ResponseEntity<List<EvaluationSessionDto>> getSessionsByDepartment(
            @PathVariable Long departmentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<EvaluationSessionDto> sessions = evaluationService.getEvaluationSessionsByDepartment(departmentId);
        return ResponseEntity.ok(sessions);
    }

    // Evaluation submission endpoint
    @PostMapping("/submit")
    public ResponseEntity<ConfirmationDto> submitEvaluation(
            @RequestBody EvaluationRequestDto request,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ConfirmationDto result = evaluationService.submitEvaluation(
            Long.parseLong(studentId), request);
        return ResponseEntity.ok(result);
    }

    // Questions and categories endpoints
    @GetMapping("/questions")
    public ResponseEntity<List<EvaluationQuestion>> getEvaluationQuestions() {
        List<EvaluationQuestion> questions = evaluationService.getEvaluationQuestions();
        return ResponseEntity.ok(questions);
    }
    
    @GetMapping("/questions/category/{categoryId}")
    public ResponseEntity<List<EvaluationQuestionDto>> getQuestionsByCategory(@PathVariable Long categoryId) {
        List<EvaluationQuestionDto> questions = evaluationService.getEvaluationQuestionsByCategory(categoryId);
        return ResponseEntity.ok(questions);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<EvaluationCategoryDto>> getEvaluationCategories() {
        List<EvaluationCategoryDto> categories = evaluationService.getEvaluationCategories();
        return ResponseEntity.ok(categories);
    }

    // Analytics endpoints
    @GetMapping("/analytics/course/{courseSessionId}/lecturer/{lecturerId}")
    public ResponseEntity<EvaluationAnalyticsResponse> getCourseEvaluationAnalytics(
            @PathVariable Long courseSessionId,
            @PathVariable Long lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        EvaluationAnalyticsResponse analytics = evaluationService.getEvaluationAnalytics(
            courseSessionId, lecturerId);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/analytics/lecturer/{lecturerId}")
    public ResponseEntity<List<EvaluationAnalyticsResponse>> getLecturerEvaluationAnalytics(
            @PathVariable Long lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<EvaluationAnalyticsResponse> analytics = evaluationService.getLecturerEvaluationAnalytics(lecturerId);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/analytics/department/{departmentId}")
    public ResponseEntity<List<EvaluationAnalyticsResponse>> getDepartmentEvaluationAnalytics(
            @PathVariable Long departmentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<EvaluationAnalyticsResponse> analytics = evaluationService.getDepartmentEvaluationAnalytics(departmentId);
        return ResponseEntity.ok(analytics);
    }
}