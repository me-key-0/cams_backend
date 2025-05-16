package com.cams.user_service.repository;

import com.cams.user_service.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByLecturerId(Long lecturerId);
    List<Evaluation> findByStudentId(Long studentId);
}
