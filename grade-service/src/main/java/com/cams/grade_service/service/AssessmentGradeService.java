package com.cams.grade_service.service;

import com.cams.grade_service.dto.AssessmentGradeDto;
import com.cams.grade_service.model.AssessmentGrade;

import java.util.List;

public interface AssessmentGradeService {
    // AssessmentGrade createAssessmentGrade(AssessmentGradeDto dto);
    // List<AssessmentGrade> getGradesForStudentInCourse(Long studentId, Long courseSessionId);
    String deleteAssessmentGrade(Long id);
}