package com.cams.grade_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentAssessmentOverviewResponse {
    private Long courseSessionId;
    private String courseCode;
    private String courseName;
    private List<AssessmentItemResponse> assessments;
    private Integer totalAssessments;
    private Integer completedAssessments;
    private Integer pendingAssessments;
    private Double overallGrade;
    private String overallLetterGrade;
}