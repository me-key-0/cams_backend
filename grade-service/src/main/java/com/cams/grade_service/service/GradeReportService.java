package com.cams.grade_service.service;

import com.cams.grade_service.dto.GradeReportDto;
import com.cams.grade_service.model.GradeReport;

public interface GradeReportService {
    GradeReport postFinalGrade(GradeReportDto dto);
    GradeReport getFinalGrade(Long studentId, Long courseSessionId);
}