package com.cams.grade_service.service;

import java.util.List;

import com.cams.grade_service.dto.CourseGradeResponseDto;
import com.cams.grade_service.dto.GradeReportDto;
import com.cams.grade_service.model.GradeReport;

public interface GradeReportService {
    GradeReport postFinalGrade(GradeReportDto dto);
    // GradeReport getFinalGrade(Long studentId, Long courseSessionId);
    List<CourseGradeResponseDto> getGradeReports(Long studentId, Integer year, Integer semester);
}