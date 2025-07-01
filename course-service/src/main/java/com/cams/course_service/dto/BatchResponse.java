package com.cams.course_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BatchResponse {
    private Long id;
    private String name;
    private Integer admissionYear;
    private Integer currentYear;
    private Integer currentSemester;
    private Long departmentId;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<CourseAssignmentResponse> courseAssignments;
    private Integer totalStudents;
}