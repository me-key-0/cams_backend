package com.cams.grade_service.dto;

import lombok.Data;

@Data
public class GradeSubmissionRequest {
    private Double score;
    private String feedback;
}