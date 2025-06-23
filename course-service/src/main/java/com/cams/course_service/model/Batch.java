package com.cams.course_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "CS-2024-Batch-1"

    @Column(nullable = false)
    private Integer admissionYear; // e.g., 2024

    @Column(nullable = false)
    private Integer currentYear; // 1, 2, 3, 4

    @Column(nullable = false)
    private Integer currentSemester; // 1 or 2

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private List<BatchCourseAssignment> courseAssignments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
}