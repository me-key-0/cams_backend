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
public class GradeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Midterm Exam", "Quiz 1", "Final Exam"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer maxScore;

    @Column(nullable = false)
    private Double weightPercentage; // Weight in final grade calculation

    @Column(nullable = false)
    private Long courseSessionId;

    @Column(nullable = false)
    private Long createdBy; // Lecturer ID

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeTypeCategory category;

    @Column(nullable = false)
    private Boolean isDefault; // Default grade types like midterm, final

    @Column(nullable = false)
    private Boolean isActive;

    // For assignment-based grade types
    private Long assignmentId;

    public enum GradeTypeCategory {
        EXAM, QUIZ, ASSIGNMENT, PROJECT, PARTICIPATION, OTHER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isDefault == null) isDefault = false;
        if (isActive == null) isActive = true;
    }
}