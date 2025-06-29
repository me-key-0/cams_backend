package com.cams.user_service.repository;

import com.cams.user_service.model.EvaluationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationCategoryRepository extends JpaRepository<EvaluationCategory, Long> {
}