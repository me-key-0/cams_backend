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
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(columnDefinition = "TEXT")
    private String content; // Text submission content

    @ElementCollection
    @CollectionTable(
        name = "submission_attachments",
        joinColumns = @JoinColumn(name = "submission_id")
    )
    @Column(name = "resource_id")
    private List<Long> attachmentIds; // Resource IDs from resource service

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    private Double score;
    private Integer maxScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(nullable = false)
    private boolean isLate;

    private LocalDateTime gradedAt;
    private Long gradedBy; // Lecturer ID

    public enum SubmissionStatus {
        PENDING, SUBMITTED, GRADED, RETURNED
    }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (status == null) {
            status = SubmissionStatus.SUBMITTED;
        }
    }
}