package com.cams.grade_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentGradeDto {
    private Long studentId;
    private Long courseId;
    private Long gradeTypeId;
    private Integer total;
    private Double gradeValue;
}


