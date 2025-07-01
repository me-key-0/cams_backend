package com.cams.grade_service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GradebookResponse {
    private Long courseSessionId;
    private String courseCode;
    private String courseName;
    private List<GradeTypeResponse> gradeTypes;
    private List<StudentGradebookRow> students;
    private Map<String, Double> classAverages; // Grade type name -> average score
}