package com.cams.grade_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSubmissionDto {
    private Long id;
    private Long assessmentId;
    private Long studentId;
    private String submissionUrl;
    private LocalDateTime submittedAt;
    private Double score;
}
