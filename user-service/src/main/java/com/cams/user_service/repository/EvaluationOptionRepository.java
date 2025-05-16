package com.cams.user_service.repository;

import com.cams.user_service.model.EvaluationOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationOptionRepository extends JpaRepository<EvaluationOption,Long> {
}
