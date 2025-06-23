package com.cams.course_service.repository;

import com.cams.course_service.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    
    List<Batch> findByDepartmentIdAndIsActiveTrueOrderByAdmissionYearDesc(Long departmentId);
    
    List<Batch> findByDepartmentIdAndCurrentYearAndCurrentSemesterAndIsActiveTrue(
        Long departmentId, Integer currentYear, Integer currentSemester);
    
    boolean existsByNameAndDepartmentId(String name, Long departmentId);
}