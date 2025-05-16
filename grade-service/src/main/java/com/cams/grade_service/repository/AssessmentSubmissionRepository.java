package com.cams.grade_service.repository;

import com.cams.grade_service.model.AssessmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission,Long> {

    List<AssessmentSubmission> findByAssessmentId(Long assessmentId);
    List<AssessmentSubmission> findByStudentId(Long studentId);
}
