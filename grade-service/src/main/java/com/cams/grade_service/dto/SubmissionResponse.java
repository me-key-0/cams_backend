package com.cams.grade_service.dto;

import com.cams.grade_service.model.Submission;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String content;
    private List<Long> attachmentIds;
    private List<AssignmentResponse.ResourceInfo> attachments;
    private LocalDateTime submittedAt;
    private Submission.SubmissionStatus status;
    private Double score;
    private Integer maxScore;
    private String feedback;
    private boolean isLate;
    private LocalDateTime gradedAt;
    private Long gradedBy;
    private String graderName;
}