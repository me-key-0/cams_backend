package com.cams.grade_service.repository;

import com.cams.grade_service.dto.GradeReportView;
import com.cams.grade_service.model.GradeReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeReportRepository extends JpaRepository<GradeReport,Long> {
    // GradeReport findByStudentIdAndCourseSessionId(Long studentId, Long courseId);
    // List<GradeReport> findByCourseSessionId(Long courseSessionId);

@Query("SELECT gr FROM GradeReport gr WHERE gr.studentId = :studentId AND gr.courseSessionId IN :sessionIds")
List<GradeReport> findByStudentIdAndCourseSessionIds(@Param("studentId") Long studentId,
                                                     @Param("sessionIds") List<Long> sessionIds);

}
