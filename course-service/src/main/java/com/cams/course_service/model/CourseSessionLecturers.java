package com.cams.course_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "course_session_lecturers")
public class CourseSessionLecturers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_session_id")
    private Long courseSessionId;

    @Column(name = "lecturer_id")
    private Long lecturerId;


}
