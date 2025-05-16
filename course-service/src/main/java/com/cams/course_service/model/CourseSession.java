package com.cams.course_service.model;

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
    private Long studentId;

    // Placeholder
    private Long departmentId;
}
