package com.cams.grade_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long studentId;
    private Long courseId;
    private Double finalGrade;

    public GradeReport(Long studentId, Long courseId, Double finalGrade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.finalGrade = finalGrade;
    }
}