package com.cams.grade_service.repository;

import com.cams.grade_service.model.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeTypeRepository extends JpaRepository<GradeType, Long> {
    
    List<GradeType> findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtAsc(Long courseSessionId);
    
    List<GradeType> findByCourseSessionIdAndIsDefaultTrueAndIsActiveTrueOrderByCreatedAtAsc(Long courseSessionId);
    
    @Query("SELECT gt FROM GradeType gt WHERE gt.courseSessionId = :courseSessionId AND gt.createdBy = :lecturerId AND gt.isActive = true ORDER BY gt.createdAt ASC")
    List<GradeType> findByLecturerAndCourseSession(@Param("courseSessionId") Long courseSessionId, @Param("lecturerId") Long lecturerId);
    
    boolean existsByAssignmentIdAndIsActiveTrue(Long assignmentId);
}