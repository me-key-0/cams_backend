package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.GradeReportDto;
import com.cams.grade_service.model.GradeReport;
import com.cams.grade_service.repository.GradeReportRepository;
import org.springframework.stereotype.Service;

@Service
public class GradeReportService implements com.cams.grade_service.service.GradeReportService {
    private GradeReportRepository reportRepo;

    @Override
    public GradeReport postFinalGrade(GradeReportDto dto) {

        GradeReport report = new GradeReport(dto.getStudentId(), dto.getCourseId(), dto.getFinalGrade());
        return reportRepo.save(report);
    }

    @Override
    public GradeReport getFinalGrade(Long studentId, Long courseId) {
        return reportRepo.findByStudentIdAndCourseId(studentId, courseId);
    }
}