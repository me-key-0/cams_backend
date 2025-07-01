package com.cams.course_service.dto;

import lombok.Data;

@Data
public class LecturerCapacityRequest {
    private Long lecturerId;
    private Long departmentId;
    private Integer maxCreditHours;
}