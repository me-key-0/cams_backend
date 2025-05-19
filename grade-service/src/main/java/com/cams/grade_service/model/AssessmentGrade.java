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

    @ManyToOne
    @JoinColumn(name = "grade_type_id")
    private GradeType gradeType;

    // @ManyToOne
    // private AssessmentSubmission submissionId;

    // private Integer Total;
    // private Long courseSessionId;

    private Double gradeValue;
}



