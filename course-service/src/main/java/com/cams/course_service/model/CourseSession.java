package com.cams.course_service.model;

import java.time.LocalDateTime;
import java.util.List;

import com.cams.course_service.model.Assignment.Status;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer academicYear;
    private Integer semester;
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Long departmentId;
    
    @ElementCollection
    @CollectionTable(
        name = "course_session_lecturers",
        joinColumns = @JoinColumn(name = "course_session_id")
    )
    @Column(name = "lecturer_id")
    private List<Long> lecturerId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Boolean isActive; // Admin can activate/deactivate sessions

    @Column(nullable = false)
    private Boolean enrollmentOpen; // Controls if students can enroll

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime activatedAt;

    @Column(nullable = false)
    private Long createdBy; // Admin ID who created this session

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = false;
        if (enrollmentOpen == null) enrollmentOpen = false;
        if (status == null) status = Status.UPCOMING;
    }
}