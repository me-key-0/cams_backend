package com.cams.communication_service.client;

import lombok.Data;

@Data
public class CourseSessionDto {
    private Long id;
    private Integer year;
    private Integer semester;
    private Integer academicYear;
    private CourseDto course;
    private String lecturerName;

    @Data
    public static class CourseDto {
        private String name;
        private String code;
        private Integer creditHour;
    }
}