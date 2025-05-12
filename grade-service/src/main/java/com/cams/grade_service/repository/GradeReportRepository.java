package com.cams.grade_service.repository;

import com.cams.grade_service.model.GradeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeReportRepository extends JpaRepository<GradeReport,Long> {
    GradeReport findByStudentIdAndCourseId(Long studentId, Long courseId);
}
