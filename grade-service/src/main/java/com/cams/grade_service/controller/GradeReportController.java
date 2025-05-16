package com.cams.grade_service.controller;

import com.cams.grade_service.dto.CourseGradeResponseDto;
import com.cams.grade_service.service.GradeReportService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grade_reports")
public class GradeReportController {

    @Autowired
    private GradeReportService gradeReportService;

    // @PostMapping
    // public ResponseEntity<GradeReport> postFinalGrade(@RequestBody GradeReportDto dto) {
    //     return new ResponseEntity<>(gradeReportService.postFinalGrade(dto), HttpStatus.CREATED);
    // }

    // @GetMapping("/student/{studentId}/course/{courseId}")
    // public ResponseEntity<GradeReport> getFinalGrade(
    //         @PathVariable Long studentId,
    //         @PathVariable Long courseId) {
    //     return ResponseEntity.ok(gradeReportService.getFinalGrade(studentId, courseId));
    // }

    @GetMapping("/student/{studentId}/{year}/{semester}")
    public ResponseEntity<List<CourseGradeResponseDto>> getFinalGrades(
            @PathVariable Long studentId,
            @PathVariable Integer year,
            @PathVariable Integer semester) {
        List<CourseGradeResponseDto> sessions = gradeReportService.getGradeReports(studentId,year,semester);
        return new ResponseEntity<List<CourseGradeResponseDto>>(sessions, HttpStatus.OK);
    }

}






