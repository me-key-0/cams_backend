package com.cams.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationRequestDto {
    private Long lecturerId;
    private Long courseSessionId;
    private List<EvaluationAnswerDto> answers;
}