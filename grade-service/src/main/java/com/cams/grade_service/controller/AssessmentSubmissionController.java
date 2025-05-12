package com.cams.grade_service.controller;

import com.cams.grade_service.dto.AssessmentSubmissionDto;
import com.cams.grade_service.model.AssessmentSubmission;
import com.cams.grade_service.service.AssessmentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class AssessmentSubmissionController {

    @Autowired
    private AssessmentSubmissionService submissionService;

    @PostMapping
    public AssessmentSubmissionDto submit(@RequestBody AssessmentSubmissionDto dto) {
        return submissionService.submitAssessment(dto);
    }

    @GetMapping("/assessment/{assessmentId}")
    public List<AssessmentSubmissionDto> getByAssessment(@PathVariable Long assessmentId) {
        return submissionService.getSubmissionsByAssessment(assessmentId);
    }

    @GetMapping("/{id}")
    public AssessmentSubmissionDto getById(@PathVariable Long id) {
        return submissionService.getSubmission(id);
    }

    @PutMapping("/{id}")
    public AssessmentSubmissionDto update(@PathVariable Long id, @RequestBody AssessmentSubmissionDto dto) {
        return submissionService.updateSubmission(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
    }
}
