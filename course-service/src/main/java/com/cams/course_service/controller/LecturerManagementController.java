package com.cams.course_service.controller;

import com.cams.course_service.dto.*;
import com.cams.course_service.service.LecturerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturer-management")
@RequiredArgsConstructor
public class LecturerManagementController {

    private final LecturerManagementService lecturerManagementService;

    @PostMapping("/capacity")
    public ResponseEntity<LecturerCapacityResponse> setLecturerCapacity(
            @RequestBody LecturerCapacityRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LecturerCapacityResponse capacity = lecturerManagementService.setLecturerCapacity(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(capacity);
    }

    @GetMapping("/capacity/{lecturerId}")
    public ResponseEntity<LecturerCapacityResponse> getLecturerCapacity(@PathVariable Long lecturerId) {
        LecturerCapacityResponse capacity = lecturerManagementService.getLecturerCapacity(lecturerId);
        return ResponseEntity.ok(capacity);
    }

    @GetMapping("/capacity/department/{departmentId}")
    public ResponseEntity<List<LecturerCapacityResponse>> getLecturerCapacitiesByDepartment(
            @PathVariable Long departmentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<LecturerCapacityResponse> capacities = lecturerManagementService.getLecturerCapacitiesByDepartment(departmentId);
        return ResponseEntity.ok(capacities);
    }

    @PostMapping("/teachable-courses")
    public ResponseEntity<List<CourseDto>> assignTeachableCourses(
            @RequestBody LecturerTeachableCoursesRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<CourseDto> courses = lecturerManagementService.assignTeachableCourses(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(courses);
    }

    @GetMapping("/teachable-courses/{lecturerId}")
    public ResponseEntity<List<CourseDto>> getTeachableCourses(@PathVariable Long lecturerId) {
        List<CourseDto> courses = lecturerManagementService.getTeachableCourses(lecturerId);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/teachable-courses/{lecturerId}/{courseId}")
    public ResponseEntity<Void> removeTeachableCourse(
            @PathVariable Long lecturerId,
            @PathVariable Long courseId,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        lecturerManagementService.removeTeachableCourse(lecturerId, courseId, Long.parseLong(adminId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate-lecturer-course")
    public ResponseEntity<Boolean> validateLecturerForCourse(
            @RequestParam Long lecturerId,
            @RequestParam Long courseId) {
        
        boolean isValid = lecturerManagementService.validateLecturerForCourse(lecturerId, courseId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate-lecturer-department")
    public ResponseEntity<Boolean> validateLecturerDepartment(
            @RequestParam Long lecturerId,
            @RequestParam Long departmentId) {
        
        boolean isValid = lecturerManagementService.validateLecturerDepartment(lecturerId, departmentId);
        return ResponseEntity.ok(isValid);
    }
}