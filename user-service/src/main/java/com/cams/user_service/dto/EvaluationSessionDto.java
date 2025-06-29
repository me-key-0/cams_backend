package com.cams.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSessionDto {
    private Long id;
    private Long courseSessionId;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long departmentId;
    private Long activatedBy;
    private String courseCode;
    private String courseName;
}