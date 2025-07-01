package com.cams.grade_service.dto;

import com.cams.grade_service.model.GradeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeTypeResponse {
    private Long id;
    private String name;
    private String description;
    private Integer maxScore;
    private Double weightPercentage;
    private Long courseSessionId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private GradeType.GradeTypeCategory category;
    private Boolean isDefault;
    private Boolean isActive;
    private Long assignmentId;
    private String assignmentTitle; // Populated if assignmentId exists
}