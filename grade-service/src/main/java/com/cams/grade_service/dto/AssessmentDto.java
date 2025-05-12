package com.cams.grade_service.dto;

import com.cams.grade_service.model.Assessment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentDto {
    private Long id;
    private String title;
    private String description;
    private Assessment.AssessmentType type; // ASSIGNMENT or PROJECT
    private boolean isGroupWork;
    private Long courseSessionId;
    private LocalDateTime deadline;
}