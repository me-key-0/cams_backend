package com.cams.course_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSessionDto {
    private Long id;
    private Integer year;
    private Integer semester;
    private Integer academicYear;
    private CourseDto course;

}

