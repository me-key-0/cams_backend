package com.cams.grade_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cams.grade_service.dto.CourseSessionDto;

@FeignClient(name = "course-service") // or use service discovery
public interface CourseServiceClient {

    @GetMapping("/api/enrollment/student/{studentId}")
    List<CourseSessionDto> getCourseSessionByStudentId(@PathVariable Long studentId);
}