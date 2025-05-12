package com.cams.grade_service.repository;

import com.cams.grade_service.model.AssessmentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentGradeRepository extends JpaRepository<AssessmentGrade,Long> {
    List<AssessmentGrade> findByStudentIdAndCourseSessionId(Long studentId,Long courseId);
}
