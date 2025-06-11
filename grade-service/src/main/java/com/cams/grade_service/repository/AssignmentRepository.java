package com.cams.grade_service.repository;

import com.cams.grade_service.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByCourseSessionIdAndStatusOrderByCreatedAtDesc(Long courseSessionId, Assignment.AssignmentStatus status);
    
    List<Assignment> findByLecturerIdOrderByCreatedAtDesc(Long lecturerId);
    
    @Query("SELECT a FROM Assignment a WHERE a.courseSessionId IN :courseSessionIds AND a.status = :status ORDER BY a.createdAt DESC")
    List<Assignment> findByCourseSessionIdsAndStatus(@Param("courseSessionIds") List<Long> courseSessionIds, 
                                                    @Param("status") Assignment.AssignmentStatus status);
    
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.status = :status")
    List<Assignment> findOverdueAssignments(@Param("now") LocalDateTime now, 
                                          @Param("status") Assignment.AssignmentStatus status);
}