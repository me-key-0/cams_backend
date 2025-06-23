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
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long courseSessionId;

    @Column(nullable = false)
    private Long lecturerId;

    @Column(nullable = false)
    private String lecturerName;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer maxScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @ElementCollection
    @CollectionTable(
        name = "assignment_attachments",
        joinColumns = @JoinColumn(name = "assignment_id")
    )
    @Column(name = "resource_id")
    private List<Long> attachmentIds; // Resource IDs from resource service

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions;

    // For group assignments
    private Boolean isGroupAssignment;

    public enum AssignmentType {
        INDIVIDUAL, GROUP
    }

    public enum AssignmentStatus {
        DRAFT, PUBLISHED, CLOSED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = AssignmentStatus.DRAFT;
        }
        if (isGroupAssignment == null) {
            isGroupAssignment = (type == AssignmentType.GROUP);
        }
    }
}