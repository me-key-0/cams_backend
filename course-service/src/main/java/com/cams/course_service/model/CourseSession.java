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

    private Long lecturerId;
    private String semester;
    private String academicYear;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
