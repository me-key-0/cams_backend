package com.cams.grade_service.controller;

import com.cams.grade_service.service.AssessmentGradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment_grades")
public class AssessmentGradeController {

    private AssessmentGradeService assessmentGradeService;

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