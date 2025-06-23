package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.model.Enrollment;
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
    
    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Students can only enroll themselves
        if ("STUDENT".equals(role) && !userId.equals(studentId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseSessionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }
    
    @DeleteMapping("/unenroll")
    public ResponseEntity<Void> unenrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Students can only unenroll themselves, admins can unenroll anyone
        if ("STUDENT".equals(role) && !userId.equals(studentId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        enrollmentService.unenrollStudent(studentId, courseSessionId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/check-enrollment")
    public ResponseEntity<Boolean> checkEnrollment(
            @RequestParam Long studentId,
            @RequestParam Long courseSessionId) {
        
        boolean isEnrolled = enrollmentService.isStudentEnrolled(studentId, courseSessionId);
        return ResponseEntity.ok(isEnrolled);
    }
    
    @GetMapping("/course-session/{courseSessionId}/students")
    public ResponseEntity<List<Long>> getEnrolledStudents(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Long> studentIds = enrollmentService.getEnrolledStudents(courseSessionId);
        return ResponseEntity.ok(studentIds);
    }
}