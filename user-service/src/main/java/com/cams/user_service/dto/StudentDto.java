package com.cams.user_service.dto;

import lombok.Data;

@Data
public class StudentDto {
    private Long id;
    // private String studentId;
    private String username;
    private String email;
    private Integer admissionYear;
    private Integer currentYear;
    private Integer currentSemester;
}