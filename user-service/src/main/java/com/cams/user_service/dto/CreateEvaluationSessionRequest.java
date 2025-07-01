package com.cams.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEvaluationSessionRequest {
    private Long courseSessionId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long departmentId;
}