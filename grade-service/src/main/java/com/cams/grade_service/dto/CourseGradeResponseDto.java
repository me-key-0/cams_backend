package com.cams.grade_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseGradeResponseDto {
    private String courseCode;
    private String courseName;
    private int creditHour;
    private Double finalGrade;
}