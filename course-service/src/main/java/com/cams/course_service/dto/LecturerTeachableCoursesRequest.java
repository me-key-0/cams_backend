package com.cams.course_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class LecturerTeachableCoursesRequest {
    private Long lecturerId;
    private List<Long> courseIds;
}