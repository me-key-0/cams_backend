package com.cams.grade_service.controller;

import com.cams.grade_service.dto.AssessmentGradeDto;
import com.cams.grade_service.model.AssessmentGrade;
import com.cams.grade_service.service.AssessmentGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment_grades")
@RequiredArgsConstructor
public class AssessmentGradeController {

    private final AssessmentGradeService assessmentGradeService;

    // @PostMapping
    // public ResponseEntity<AssessmentGrade> postAssessmentGrade(@RequestBody AssessmentGradeDto dto) {
    //     return new ResponseEntity<>(assessmentGradeService.createAssessmentGrade(dto), HttpStatus.CREATED);
    // }

    // @GetMapping("/student/{studentId}/course/{courseId}")
    // public ResponseEntity<List<AssessmentGrade>> getGradesForStudentInCourse(
    //         @PathVariable Long studentId,
    //         @PathVariable Long courseId) {
    //     return ResponseEntity.ok(assessmentGradeService.getGradesForStudentInCourse(studentId, courseId));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGrade(
            @PathVariable Long id) {
        return ResponseEntity.ok(assessmentGradeService.deleteAssessmentGrade(id));
    }
}