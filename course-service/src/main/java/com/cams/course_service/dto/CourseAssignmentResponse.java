package com.cams.course_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseAssignmentResponse {
    private Long id;
    private Long batchId;
    private String batchName;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer creditHour;
    private Integer year;
    private Integer semester;
    private Long assignedBy;
    private LocalDateTime assignedAt;
    private Boolean isActive;
}