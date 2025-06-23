package com.cams.grade_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkGradeRequest {
    private Long gradeTypeId;
    private List<StudentGradeRequest> grades;
    private String feedback; // Common feedback for all students
}