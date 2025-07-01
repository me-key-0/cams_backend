package com.cams.grade_service.controller;

import com.cams.grade_service.dto.*;
import com.cams.grade_service.service.GradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/grades/grading")
@RequiredArgsConstructor
public class GradingController {

    private final GradingService gradingService;

    // Grade Type Management
    @PostMapping("/grade-types")
    public ResponseEntity<GradeTypeResponse> createGradeType(
            @RequestBody GradeTypeRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        GradeTypeResponse gradeType = gradingService.createGradeType(request, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeType);
    }

    @PutMapping("/grade-types/{id}")
    public ResponseEntity<GradeTypeResponse> updateGradeType(
            @PathVariable Long id,
            @RequestBody GradeTypeRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GradeTypeResponse gradeType = gradingService.updateGradeType(id, request, Long.parseLong(lecturerId));
        return ResponseEntity.ok(gradeType);
    }

    @DeleteMapping("/grade-types/{id}")
    public ResponseEntity<Void> deleteGradeType(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        gradingService.deleteGradeType(id, Long.parseLong(lecturerId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grade-types/course-session/{courseSessionId}")
    public ResponseEntity<List<GradeTypeResponse>> getGradeTypesByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Role") String role) {
        
        List<GradeTypeResponse> gradeTypes = gradingService.getGradeTypesByCourseSession(courseSessionId);
        return ResponseEntity.ok(gradeTypes);
    }

    @GetMapping("/grade-types/{id}")
    public ResponseEntity<GradeTypeResponse> getGradeType(@PathVariable Long id) {
        GradeTypeResponse gradeType = gradingService.getGradeTypeById(id);
        return ResponseEntity.ok(gradeType);
    }

    @PostMapping("/grade-types/course-session/{courseSessionId}/defaults")
    public ResponseEntity<Void> createDefaultGradeTypes(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        gradingService.createDefaultGradeTypes(courseSessionId, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.ok().build();
    }

    // Student Grade Management
    @PostMapping("/grades")
    public ResponseEntity<StudentGradeResponse> addOrUpdateGrade(
            @RequestBody StudentGradeRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        StudentGradeResponse grade = gradingService.addOrUpdateGrade(request, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.ok(grade);
    }

    @PostMapping("/grades/bulk")
    public ResponseEntity<List<StudentGradeResponse>> addBulkGrades(
            @RequestBody BulkGradeRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        List<StudentGradeResponse> grades = gradingService.addBulkGrades(request, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.ok(grades);
    }

    @DeleteMapping("/grades/student/{studentId}/grade-type/{gradeTypeId}")
    public ResponseEntity<Void> deleteGrade(
            @PathVariable Long studentId,
            @PathVariable Long gradeTypeId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        gradingService.deleteGrade(studentId, gradeTypeId, Long.parseLong(lecturerId));
        return ResponseEntity.noContent().build();
    }

    // Gradebook Management
    @GetMapping("/gradebook/course-session/{courseSessionId}")
    public ResponseEntity<GradebookResponse> getGradebook(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GradebookResponse gradebook = gradingService.getGradebook(courseSessionId, Long.parseLong(lecturerId));
        return ResponseEntity.ok(gradebook);
    }

    @GetMapping("/gradebook/course-session/{courseSessionId}/export")
    public ResponseEntity<byte[]> exportGradebook(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        byte[] excelData = gradingService.exportGradebookToExcel(courseSessionId, Long.parseLong(lecturerId));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "gradebook.xlsx");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
    }

    @PostMapping("/gradebook/course-session/{courseSessionId}/import")
    public ResponseEntity<List<StudentGradeResponse>> importGrades(
            @PathVariable Long courseSessionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("gradeTypeId") Long gradeTypeId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        List<StudentGradeResponse> grades = gradingService.importGradesFromExcel(
            file, courseSessionId, gradeTypeId, Long.parseLong(lecturerId), lecturerName);
        
        return ResponseEntity.ok(grades);
    }

    // Student Assessment Overview
    @GetMapping("/assessments/student/{studentId}/course-session/{courseSessionId}")
    public ResponseEntity<StudentAssessmentOverviewResponse> getStudentAssessmentOverview(
            @PathVariable Long studentId,
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Students can only view their own assessments
        if ("STUDENT".equals(role) && !userId.equals(studentId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        StudentAssessmentOverviewResponse overview = gradingService.getStudentAssessmentOverview(studentId, courseSessionId);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/assessments/student/{studentId}/course-session/{courseSessionId}/list")
    public ResponseEntity<List<AssessmentItemResponse>> getStudentAssessments(
            @PathVariable Long studentId,
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Students can only view their own assessments
        if ("STUDENT".equals(role) && !userId.equals(studentId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<AssessmentItemResponse> assessments = gradingService.getStudentAssessments(studentId, courseSessionId);
        return ResponseEntity.ok(assessments);
    }

    // Student endpoints for their own assessments
    @GetMapping("/my-assessments/course-session/{courseSessionId}")
    public ResponseEntity<StudentAssessmentOverviewResponse> getMyAssessmentOverview(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        StudentAssessmentOverviewResponse overview = gradingService.getStudentAssessmentOverview(Long.parseLong(studentId), courseSessionId);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/my-assessments/course-session/{courseSessionId}/list")
    public ResponseEntity<List<AssessmentItemResponse>> getMyAssessments(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<AssessmentItemResponse> assessments = gradingService.getStudentAssessments(Long.parseLong(studentId), courseSessionId);
        return ResponseEntity.ok(assessments);
    }
}