package com.cams.grade_service.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StudentGradebookRow {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Map<Long, StudentGradeResponse> grades; // GradeType ID -> Grade
    private Double totalScore;
    private Double finalGrade;
    private String letterGrade;
}