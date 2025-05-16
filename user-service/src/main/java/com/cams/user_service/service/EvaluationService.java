package com.cams.user_service.service;

import com.cams.user_service.dto.ConfirmationDto;
import com.cams.user_service.dto.EvaluationRequestDto;
import com.cams.user_service.model.EvaluationQuestion;

import java.util.List;

public interface EvaluationService {
    ConfirmationDto activateEvalSession(Long sessionId);
    ConfirmationDto isEvalSessionActivated(Long sessionId);
    ConfirmationDto submitEvaluation(Long studentId, EvaluationRequestDto request);
    List<EvaluationQuestion> getEvaluationQuestions();
}
