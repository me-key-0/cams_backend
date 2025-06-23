package com.cams.course_service.controller;

import com.cams.course_service.dto.BatchRequest;
import com.cams.course_service.dto.BatchResponse;
import com.cams.course_service.dto.CourseAssignmentRequest;
import com.cams.course_service.dto.CourseAssignmentResponse;
import com.cams.course_service.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(
            @RequestBody BatchRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        BatchResponse batch = batchService.createBatch(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(batch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> updateBatch(
            @PathVariable Long id,
            @RequestBody BatchRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        BatchResponse batch = batchService.updateBatch(id, request, Long.parseLong(adminId));
        return ResponseEntity.ok(batch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        batchService.deleteBatch(id, Long.parseLong(adminId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getBatch(@PathVariable Long id) {
        BatchResponse batch = batchService.getBatchById(id);
        return ResponseEntity.ok(batch);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<BatchResponse>> getBatchesByDepartment(
            @PathVariable Long departmentId,
            @RequestHeader("X-User-Role") String role) {
        
        List<BatchResponse> batches = batchService.getBatchesByDepartment(departmentId);
        return ResponseEntity.ok(batches);
    }

    @PostMapping("/course-assignments")
    public ResponseEntity<List<CourseAssignmentResponse>> assignCoursesToBatch(
            @RequestBody CourseAssignmentRequest request,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<CourseAssignmentResponse> assignments = batchService.assignCoursesToBatch(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(assignments);
    }

    @GetMapping("/{batchId}/course-assignments")
    public ResponseEntity<List<CourseAssignmentResponse>> getCourseAssignmentsForBatch(@PathVariable Long batchId) {
        List<CourseAssignmentResponse> assignments = batchService.getCourseAssignmentsForBatch(batchId);
        return ResponseEntity.ok(assignments);
    }

    @DeleteMapping("/course-assignments/{assignmentId}")
    public ResponseEntity<Void> removeCourseAssignment(
            @PathVariable Long assignmentId,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        batchService.removeCourseAssignment(assignmentId, Long.parseLong(adminId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{batchId}/advance-semester")
    public ResponseEntity<BatchResponse> advanceBatchSemester(
            @PathVariable Long batchId,
            @RequestHeader("X-User-Id") String adminId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        BatchResponse batch = batchService.advanceBatchSemester(batchId, Long.parseLong(adminId));
        return ResponseEntity.ok(batch);
    }

    @GetMapping("/{batchId}/validate-credit-hours")
    public ResponseEntity<Boolean> validateCreditHours(
            @PathVariable Long batchId,
            @RequestParam Integer year,
            @RequestParam Integer semester,
            @RequestParam List<Long> courseIds) {
        
        boolean isValid = batchService.validateCreditHours(batchId, year, semester, courseIds);
        return ResponseEntity.ok(isValid);
    }
}