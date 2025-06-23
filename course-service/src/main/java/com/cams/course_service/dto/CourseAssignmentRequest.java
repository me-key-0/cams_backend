package com.cams.course_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseAssignmentRequest {
    private Long batchId;
    private List<CourseAssignmentItem> courses;
    
    @Data
    public static class CourseAssignmentItem {
        private Long courseId;
        private Integer year;
        private Integer semester;
    }
}