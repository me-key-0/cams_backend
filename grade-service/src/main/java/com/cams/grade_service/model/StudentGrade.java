package com.cams.grade_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_type_id", nullable = false)
    private GradeType gradeType;

    @Column(nullable = false)
    private Double score;

    private String feedback;

    @Column(nullable = false)
    private Long gradedBy; // Lecturer ID

    @Column(nullable = false)
    private String graderName;

    @Column(nullable = false)
    private LocalDateTime gradedAt;

    // For group assignments
    private Long groupId;

    @PrePersist
    protected void onCreate() {
        gradedAt = LocalDateTime.now();
    }
}