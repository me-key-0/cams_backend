package com.cams.grade_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentCreateRequest {
    private String title;
    private String description;
    private Long courseSessionId;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private AssignmentType type;
    private List<Long> attachmentIds; // Resource IDs

    public enum AssignmentType {
        INDIVIDUAL, GROUP
    }
}