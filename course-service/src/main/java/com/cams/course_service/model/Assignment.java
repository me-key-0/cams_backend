package com.cams.course_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lecturerId;

    
    @Enumerated(EnumType.STRING)
    private Status status; 

    @ManyToOne
    @JoinColumn(name = "course_session_id")
    private CourseSession courseSession;
    
    public enum Status {
        ACTIVE,
        UPCOMING,
        COMPLETED
    }
    
}


