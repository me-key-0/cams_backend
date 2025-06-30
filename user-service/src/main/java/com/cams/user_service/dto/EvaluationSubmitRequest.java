package com.cams.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationSubmitRequest {
    private Long courseSessionId;
    private List<EvaluationAnswerDto> answers;
}