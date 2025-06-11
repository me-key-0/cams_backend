package com.cams.grade_service.dto;

import com.cams.grade_service.model.Assignment;
import com.cams.grade_service.model.Submission;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentAssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private Assignment.AssignmentType type;
    private List<AssignmentResponse.ResourceInfo> attachments;
    
    // Submission info
    private Submission.SubmissionStatus submissionStatus;
    private LocalDateTime submittedAt;
    private Double score;
    private String feedback;
    private boolean isLate;
    private boolean isOverdue;
    
    // Computed fields
    private String statusDisplay; // "pending", "submitted", "graded"
    private String gradeDisplay; // "18/20" or null
}