package com.cams.grade_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AssessmentGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long courseSessionId;

    @ManyToOne
    @JoinColumn(name = "grade_type_id")
    private GradeType gradeType;

    private Integer Total;

    private Double gradeValue;
}