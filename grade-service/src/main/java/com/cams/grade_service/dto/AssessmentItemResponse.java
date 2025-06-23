package com.cams.grade_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentItemResponse {
    private Long id;
    private String title;
    private String description;
    private String type; // "ASSIGNMENT", "EXAM", "QUIZ", etc.
    private LocalDateTime dueDate;
    private Integer maxScore;
    private String status; // "pending", "submitted", "graded"
    private Double score;
    private String feedback;
    private Boolean isLate;
    private Boolean isOverdue;
    private List<AttachmentInfo> attachments;
    private String gradeDisplay; // "85/100" or "pending"
    
    @Data
    public static class AttachmentInfo {
        private Long id;
        private String title;
        private String fileName;
        private String downloadUrl;
    }
}