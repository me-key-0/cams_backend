package com.cams.grade_service.controller;

import com.cams.grade_service.dto.*;
import com.cams.grade_service.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    // Assignment Management (Lecturer)
    @PostMapping
    public ResponseEntity<AssignmentResponse> createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        AssignmentResponse assignment = assignmentService.createAssignment(request, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentCreateRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        AssignmentResponse assignment = assignmentService.updateAssignment(id, request, Long.parseLong(lecturerId));
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        assignmentService.deleteAssignment(id, Long.parseLong(lecturerId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        AssignmentResponse assignment = assignmentService.getAssignmentById(id, Long.parseLong(userId), role);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<AssignmentResponse> publishAssignment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        AssignmentResponse assignment = assignmentService.publishAssignment(id, Long.parseLong(lecturerId));
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<AssignmentResponse> closeAssignment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        AssignmentResponse assignment = assignmentService.closeAssignment(id, Long.parseLong(lecturerId));
        return ResponseEntity.ok(assignment);
    }

    // Assignment Listing
    @GetMapping("/course-session/{courseSessionId}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByCourseSession(
            courseSessionId, Long.parseLong(userId), role);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/my-assignments")
    public ResponseEntity<List<AssignmentResponse>> getMyAssignments(
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<AssignmentResponse> assignments = assignmentService.getMyAssignments(Long.parseLong(lecturerId));
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/student")
    public ResponseEntity<List<StudentAssignmentResponse>> getStudentAssignments(
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<StudentAssignmentResponse> assignments = assignmentService.getStudentAssignments(Long.parseLong(studentId));
        return ResponseEntity.ok(assignments);
    }

    // Submission Management (Student)
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String studentName = "Student " + studentId; // Should be fetched from user service
        SubmissionResponse submission = assignmentService.submitAssignment(request, Long.parseLong(studentId), studentName);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @PutMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        SubmissionResponse submission = assignmentService.updateSubmission(submissionId, request, Long.parseLong(studentId));
        return ResponseEntity.ok(submission);
    }

    @DeleteMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Long submissionId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        assignmentService.deleteSubmission(submissionId, Long.parseLong(studentId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/submissions/my-submissions")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissions(
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<SubmissionResponse> submissions = assignmentService.getMySubmissions(Long.parseLong(studentId));
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{assignmentId}/my-submission")
    public ResponseEntity<SubmissionResponse> getMySubmissionForAssignment(
            @PathVariable Long assignmentId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        SubmissionResponse submission = assignmentService.getMySubmissionForAssignment(assignmentId, Long.parseLong(studentId));
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(submission);
    }

    // Grading (Lecturer)
    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getAssignmentSubmissions(
            @PathVariable Long assignmentId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<SubmissionResponse> submissions = assignmentService.getAssignmentSubmissions(assignmentId, Long.parseLong(lecturerId));
        return ResponseEntity.ok(submissions);
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        SubmissionResponse submission = assignmentService.gradeSubmission(submissionId, request, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @PathVariable Long submissionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        SubmissionResponse submission = assignmentService.getSubmissionById(submissionId, Long.parseLong(userId), role);
        return ResponseEntity.ok(submission);
    }
}