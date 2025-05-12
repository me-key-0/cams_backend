package com.cams.grade_service.repository;

import com.cams.grade_service.dto.AssessmentDto;
import com.cams.grade_service.model.Assessment;
import com.cams.grade_service.model.AssessmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment,Long> {
    List<AssessmentDto> findByCourseSessionId(Long courseSessionId);
}
