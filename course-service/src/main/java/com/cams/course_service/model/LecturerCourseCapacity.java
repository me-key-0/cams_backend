package com.cams.course_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturerCourseCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long lecturerId;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private Integer maxCreditHours; // Maximum credit hours a lecturer can teach

    @Column(nullable = false)
    private Integer currentCreditHours; // Current assigned credit hours

    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        if (isActive == null) isActive = true;
        if (currentCreditHours == null) currentCreditHours = 0;
    }
}