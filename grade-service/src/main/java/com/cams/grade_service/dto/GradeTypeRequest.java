package com.cams.grade_service.dto;

import com.cams.grade_service.model.GradeType;
import lombok.Data;

@Data
public class GradeTypeRequest {
    private String name;
    private String description;
    private Integer maxScore;
    private Double weightPercentage;
    private Long courseSessionId;
    private GradeType.GradeTypeCategory category;
    private Long assignmentId; // Optional, for assignment-based grade types
}