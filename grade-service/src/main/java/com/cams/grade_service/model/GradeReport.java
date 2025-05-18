package com.cams.grade_service.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

    private Long courseSessionId;

    @OneToMany
    @JoinColumn(name = "assessment_grade_id")
    private List<AssessmentGrade> assessmentGrade;

    private Double finalGrade;

    public GradeReport(Long studentId, Long courseId, Double finalGrade) {
        this.studentId = studentId;
        this.courseSessionId = courseId;
        this.finalGrade = finalGrade;
    }
}



