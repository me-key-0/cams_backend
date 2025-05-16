package com.cams.grade_service.dto;

import lombok.Data;

@Data

public class CourseSessionDto {
    private Long id;
    private Integer year;
    private Integer semester;
    private Integer academicYear;
    private CourseDto course;

    @Data
    public static class CourseDto {
        private String name;
        private String code;
        private Integer creditHour;
    }
}

