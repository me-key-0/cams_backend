package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/sessions/{studentId}/{year}/{semester}/{academicYear}")
    public List<CourseSessionDto> getStudentCourseSessions(
            @PathVariable Long studentId,
            @PathVariable Integer year,
            @PathVariable Integer semester,
            @PathVariable Integer academicYear) {
        return enrollmentService.getCourseSessions(studentId, year, semester, academicYear);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseSessionDto>> getCourseSessionByStudentId(@PathVariable Long studentId) {
        try {
            System.out.println("Received request for student ID: " + studentId);
            List<CourseSessionDto> dto = enrollmentService.getCourseSessionsByStudent(studentId);
            System.out.println("Found " + (dto != null ? dto.size() : "null") + " course sessions");
            
            if (dto == null || dto.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<List<CourseSessionDto>>(dto, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
