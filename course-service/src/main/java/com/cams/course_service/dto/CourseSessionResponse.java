package com.cams.course_service.dto;

import com.cams.course_service.model.Assignment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseSessionResponse {
    private Long id;
    private Integer academicYear;
    private Integer semester;
    private Integer year;
    private CourseDto course;
    private Long departmentId;
    private List<LecturerInfo> lecturers;
    private Assignment.Status status;
    private Boolean isActive;
    private Boolean enrollmentOpen;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private Long createdBy;
    private Integer enrolledStudents;
    private Long batchId; // Added batch reference
    private String batchName; // Added batch name
    
    @Data
    public static class LecturerInfo {
        private Long id;
        private String name;
        private String email;
    }
}