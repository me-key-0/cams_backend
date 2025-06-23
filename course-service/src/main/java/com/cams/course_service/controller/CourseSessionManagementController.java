package com.cams.course_service.controller;

import com.cams.course_service.dto.CourseSessionRequest;
import com.cams.course_service.dto.CourseSessionResponse;
import com.cams.course_service.service.CourseSessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-sessions")
@RequiredArgsConstructor
public class CourseSessionManagementController {

    private final CourseSessionManagementService courseSessionService;

    @PostMapping
    public ResponseEntity<CourseSessionResponse> createCourseSession(
            @RequestBody CourseSessionRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.createCourseSession(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSessionResponse> updateCourseSession(
            @PathVariable Long id,
            @RequestBody CourseSessionRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.updateCourseSession(id, request, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseSession(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        courseSessionService.deleteCourseSession(id, Long.parseLong(adminId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseSessionResponse> getCourseSession(@PathVariable Long id) {
        CourseSessionResponse session = courseSessionService.getCourseSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<CourseSessionResponse>> getCourseSessionsByDepartment(@PathVariable Long departmentId) {
        List<CourseSessionResponse> sessions = courseSessionService.getCourseSessionsByDepartment(departmentId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<CourseSessionResponse> activateCourseSession(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.activateCourseSession(id, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<CourseSessionResponse> deactivateCourseSession(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.deactivateCourseSession(id, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/open-enrollment")
    public ResponseEntity<CourseSessionResponse> openEnrollment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.openEnrollment(id, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/close-enrollment")
    public ResponseEntity<CourseSessionResponse> closeEnrollment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.closeEnrollment(id, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/lecturers")
    public ResponseEntity<CourseSessionResponse> assignLecturers(
            @PathVariable Long id,
            @RequestBody List<Long> lecturerIds,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.assignLecturers(id, lecturerIds, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{id}/lecturers/{lecturerId}")
    public ResponseEntity<CourseSessionResponse> removeLecturer(
            @PathVariable Long id,
            @PathVariable Long lecturerId,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseSessionResponse session = courseSessionService.removeLecturer(id, lecturerId, Long.parseLong(adminId));
        return ResponseEntity.ok(session);
    }

    @GetMapping("/validate-lecturer-capacity")
    public ResponseEntity<Boolean> validateLecturerCapacity(
            @RequestParam Long lecturerId,
            @RequestParam Long courseId) {
        
        boolean isValid = courseSessionService.validateLecturerCapacity(lecturerId, courseId);
        return ResponseEntity.ok(isValid);
    }
}