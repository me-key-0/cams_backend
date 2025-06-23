package com.cams.grade_service.dto;

import lombok.Data;

@Data
public class StudentGradeRequest {
    private Long studentId;
    private Long gradeTypeId;
    private Double score;
    private String feedback;
    private Long groupId; // Optional, for group assignments
}