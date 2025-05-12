package com.cams.grade_service.repository;

import com.cams.grade_service.model.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeTypeRepository extends JpaRepository<GradeType,Long> {
}
