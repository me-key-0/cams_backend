package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cams.course_service.model.CourseSession;
import com.cams.course_service.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/sessions/{studentId}/{year}/{semester}/{academicYear}")
    public List<CourseSession> getStudentCourseSessions(
            @PathVariable Long studentId,
            @PathVariable Integer year,
            @PathVariable Integer semester,
            @PathVariable Integer academicYear) {
        return enrollmentService.getCourseSessions(studentId, year, semester, academicYear);
    }
}
