package com.cams.grade_service.controller;

import com.cams.grade_service.dto.AssessmentDto;
import com.cams.grade_service.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping
    public AssessmentDto create(@RequestBody AssessmentDto dto) {
        return assessmentService.createAssessment(dto);
    }

    @GetMapping
    public List<AssessmentDto> getAll() {
        return assessmentService.getAllAssessments();
    }

    @GetMapping("/{id}")
    public AssessmentDto getById(@PathVariable Long id) {
        return assessmentService.getAssessment(id);
    }

    @GetMapping("/course/{courseId}")
    public List<AssessmentDto> getByCourseId(@PathVariable Long courseId) {
        return assessmentService.getAssessmentByCourseId(courseId);
    }

    @PutMapping("/{id}")
    public AssessmentDto update(@PathVariable Long id, @RequestBody AssessmentDto dto) {
        return assessmentService.updateAssessment(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
    }
}
