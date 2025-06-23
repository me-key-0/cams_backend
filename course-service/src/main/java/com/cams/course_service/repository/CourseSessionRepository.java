package com.cams.course_service.repository;

import com.cams.course_service.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
    // Find course sessions by batch
    List<CourseSession> findByBatchId(Long batchId);
    
    // Find course sessions by batch, year, and semester
    List<CourseSession> findByBatchIdAndYearAndSemester(Long batchId, Integer year, Integer semester);
    
    // Find course sessions by department
    List<CourseSession> findByDepartmentId(Long departmentId);
    
    // Find active course sessions by batch
    List<CourseSession> findByBatchIdAndIsActiveTrue(Long batchId);
    
    // Find course sessions with open enrollment by batch
    List<CourseSession> findByBatchIdAndIsActiveTrueAndEnrollmentOpenTrue(Long batchId);
    
    // Find course sessions by lecturer
    @Query("SELECT cs FROM CourseSession cs JOIN cs.lecturerId l WHERE l = :lecturerId")
    List<CourseSession> findByLecturerId(@Param("lecturerId") Long lecturerId);
    
    // Find course sessions by course
    List<CourseSession> findByCourseId(Long courseId);
}