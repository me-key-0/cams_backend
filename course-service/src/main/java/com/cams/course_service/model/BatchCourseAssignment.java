package com.cams.course_service.model;

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
public class BatchCourseAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer year; // Which year this course is assigned to (1, 2, 3, 4)

    @Column(nullable = false)
    private Integer semester; // Which semester (1 or 2)

    @Column(nullable = false)
    private Long assignedBy; // Admin ID who assigned this course

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
}