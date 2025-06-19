package com.cams.grade_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmissionCreateRequest {
    private Long assignmentId;
    private String content; // Text content
    private List<Long> attachmentIds; // Resource IDs from resource service
}