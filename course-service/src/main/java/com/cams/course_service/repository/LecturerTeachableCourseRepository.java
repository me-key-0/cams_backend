package com.cams.course_service.repository;

import com.cams.course_service.model.LecturerTeachableCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturerTeachableCourseRepository extends JpaRepository<LecturerTeachableCourse, Long> {
    
    List<LecturerTeachableCourse> findByLecturerIdAndIsActiveTrueOrderByAssignedAtDesc(Long lecturerId);
    
    @Query("SELECT ltc FROM LecturerTeachableCourse ltc JOIN ltc.course c " +
           "WHERE ltc.lecturerId = :lecturerId AND c.departmentId = :departmentId " +
           "AND ltc.isActive = true ORDER BY c.name ASC")
    List<LecturerTeachableCourse> findByLecturerAndDepartment(@Param("lecturerId") Long lecturerId, 
                                                             @Param("departmentId") String departmentId);
    
    boolean existsByLecturerIdAndCourseIdAndIsActiveTrue(Long lecturerId, Long courseId);
    
    void deleteByLecturerIdAndCourseId(Long lecturerId, Long courseId);
}