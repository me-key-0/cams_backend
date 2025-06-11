package com.cams.grade_service.dto;

import com.cams.grade_service.model.Assignment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private Long courseSessionId;
    private Long lecturerId;
    private String lecturerName;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private Integer maxScore;
    private Assignment.AssignmentType type;
    private Assignment.AssignmentStatus status;
    private List<Long> attachmentIds;
    private List<ResourceInfo> attachments; // Populated from resource service
    private int submissionCount;
    private boolean isOverdue;
    
    // For student view
    private SubmissionResponse mySubmission;
    
    @Data
    public static class ResourceInfo {
        private Long id;
        private String title;
        private String fileName;
        private String downloadUrl;
        private String fileType;
        private Long fileSize;
    }
}