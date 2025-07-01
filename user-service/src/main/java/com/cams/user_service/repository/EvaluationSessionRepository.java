package com.cams.user_service.repository;

import com.cams.user_service.model.EvaluationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EvaluationSessionRepository extends JpaRepository<EvaluationSession, Long> {
    Optional<EvaluationSession> findByCourseSessionId(Long courseSessionId);
    List<EvaluationSession> findByDepartmentId(Long departmentId);
    List<EvaluationSession> findByIsActiveTrue();
}