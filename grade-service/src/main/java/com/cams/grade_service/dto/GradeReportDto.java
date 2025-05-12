package com.cams.grade_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeReportDto {
    private Long studentId;
    private Long courseId;
    private Double finalGrade;
}

