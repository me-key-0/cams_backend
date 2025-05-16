package com.cams.grade_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseSessionId; // reference to course-service via gRPC or REST

    @Enumerated(EnumType.STRING)
    private AssessmentType type; // PROJECT or ASSIGNMENT

    private String title;

    private String description;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private SubmissionMode submissionMode; // INDIVIDUAL or GROUP

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentSubmission> submissions;

    public enum AssessmentType {
        ASSIGNMENT,
        PROJECT
    }

    public enum SubmissionMode {
        INDIVIDUAL,
        GROUP
    }
}
