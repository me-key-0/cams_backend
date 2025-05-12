package com.cams.grade_service.service;

import com.cams.grade_service.dto.AssessmentDto;
import com.cams.grade_service.model.Assessment;

import java.util.List;

public interface AssessmentService {
    AssessmentDto createAssessment(AssessmentDto dto);
    AssessmentDto updateAssessment(Long id, AssessmentDto dto);
    void deleteAssessment(Long id);
    AssessmentDto getAssessment(Long id);
    List<AssessmentDto> getAllAssessments();
    List<AssessmentDto> getAssessmentByCourseId(Long courseId);
}