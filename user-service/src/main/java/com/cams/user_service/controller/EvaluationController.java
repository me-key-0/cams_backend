package com.cams.user_service.controller;

import com.cams.user_service.dto.ConfirmationDto;
import com.cams.user_service.dto.EvaluationRequestDto;
import com.cams.user_service.model.EvaluationQuestion;
import com.cams.user_service.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/submit/{studentId}")
    public ConfirmationDto submitEvaluation(
            @PathVariable Long studentId,
            @RequestBody EvaluationRequestDto request
    ) {
        return evaluationService.submitEvaluation(studentId, request);
    }

    @PostMapping("/session/{sessionId}")
    public ConfirmationDto activateSession(@PathVariable Long sessionId) {
        return evaluationService.activateEvalSession(sessionId);
    }

    @GetMapping("/session/{sessionId}")
    public ConfirmationDto isSessionActive(@PathVariable Long sessionId) {
        return evaluationService.isEvalSessionActivated(sessionId);
    }

    @GetMapping("/questions")
    public List<EvaluationQuestion> getEvaluationQuestions() {
        return evaluationService.getEvaluationQuestions();
    }
}
