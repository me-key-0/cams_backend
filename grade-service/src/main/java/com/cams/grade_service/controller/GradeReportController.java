package com.cams.grade_service.controller;

import com.cams.grade_service.dto.GradeReportDto;
import com.cams.grade_service.model.GradeReport;
import com.cams.grade_service.service.GradeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grade-reports")
@RequiredArgsConstructor
public class GradeReportController {

    private final GradeReportService gradeReportService;

    @PostMapping
    public ResponseEntity<GradeReport> postFinalGrade(@RequestBody GradeReportDto dto) {
        return new ResponseEntity<>(gradeReportService.postFinalGrade(dto), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<GradeReport> getFinalGrade(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeReportService.getFinalGrade(studentId, courseId));
    }
}