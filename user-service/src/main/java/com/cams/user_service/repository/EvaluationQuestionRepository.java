package com.cams.user_service.repository;

import com.cams.user_service.model.EvaluationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationQuestionRepository extends JpaRepository<EvaluationQuestion, Long> {

}