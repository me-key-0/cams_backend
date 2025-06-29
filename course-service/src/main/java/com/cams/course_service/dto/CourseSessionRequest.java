package com.cams.course_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseSessionRequest {
    private Integer academicYear;
    private Integer semester;
    private Integer year;
    private Long courseId;
    private Long departmentId;
    private List<Long> lecturerIds;
    private Long batchId; // Added batch reference
}