package com.cams.course_service.model;

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

    // Placeholder
    // private Long studentId;

    // Placeholder
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
}
