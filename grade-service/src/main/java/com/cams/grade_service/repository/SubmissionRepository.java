package com.cams.grade_service.repository;

import com.cams.grade_service.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByAssignmentIdOrderBySubmittedAtDesc(Long assignmentId);
    
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.id IN :assignmentIds AND s.studentId = :studentId")
    List<Submission> findByAssignmentIdsAndStudentId(@Param("assignmentIds") List<Long> assignmentIds, 
                                                    @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    int countByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.lecturerId = :lecturerId ORDER BY s.submittedAt DESC")
    List<Submission> findByLecturerId(@Param("lecturerId") Long lecturerId);
}