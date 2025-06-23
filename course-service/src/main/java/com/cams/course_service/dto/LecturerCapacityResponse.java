package com.cams.course_service.dto;

import lombok.Data;

@Data
public class LecturerCapacityResponse {
    private Long id;
    private Long lecturerId;
    private String lecturerName;
    private Long departmentId;
    private Integer maxCreditHours;
    private Integer currentCreditHours;
    private Integer availableCreditHours;
    private Boolean isActive;
}