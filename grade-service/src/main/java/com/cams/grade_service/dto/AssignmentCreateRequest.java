package com.cams.grade_service.dto;

import com.cams.grade_service.model.Assignment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Course session ID is required")
    private Long courseSessionId;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Max score is required")
    @Positive(message = "Max score must be positive")
    private Integer maxScore;

    @NotNull(message = "Assignment type is required")
    private Assignment.AssignmentType type;

    private List<Long> attachmentIds; // Resource IDs
}