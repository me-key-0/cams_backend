package com.cams.course_service.repository;

import com.cams.course_service.model.BatchCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchCourseAssignmentRepository extends JpaRepository<BatchCourseAssignment, Long> {
    
    List<BatchCourseAssignment> findByBatchIdAndIsActiveTrueOrderByYearAscSemesterAsc(Long batchId);
    
    List<BatchCourseAssignment> findByBatchIdAndYearAndSemesterAndIsActiveTrue(
        Long batchId, Integer year, Integer semester);
    
    boolean existsByBatchIdAndCourseIdAndIsActiveTrue(Long batchId, Long courseId);
    
    @Query("SELECT SUM(c.creditHour) FROM BatchCourseAssignment bca " +
           "JOIN bca.course c WHERE bca.batch.id = :batchId AND bca.year = :year " +
           "AND bca.semester = :semester AND bca.isActive = true")
    Integer getTotalCreditHoursForBatchSemester(@Param("batchId") Long batchId, 
                                               @Param("year") Integer year, 
                                               @Param("semester") Integer semester);
}