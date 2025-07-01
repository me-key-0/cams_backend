package com.cams.course_service.dto;

import lombok.Data;

@Data
public class BatchRequest {
    private String name;
    private Integer admissionYear;
    private Integer currentYear;
    private Integer currentSemester;
    private Long departmentId;
}