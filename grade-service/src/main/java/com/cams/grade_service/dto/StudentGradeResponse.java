package com.cams.grade_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentGradeResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long gradeTypeId;
    private String gradeTypeName;
    private Integer maxScore;
    private Double score;
    private String feedback;
    private Long gradedBy;
    private String graderName;
    private LocalDateTime gradedAt;
    private Long groupId;
    private String groupName;
}