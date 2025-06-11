package com.cams.grade_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmissionCreateRequest {
    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    private String content; // Text content

    private List<Long> attachmentIds; // Resource IDs from resource service
}