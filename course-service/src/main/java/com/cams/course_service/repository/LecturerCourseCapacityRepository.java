package com.cams.course_service.repository;

import com.cams.course_service.model.LecturerCourseCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerCourseCapacityRepository extends JpaRepository<LecturerCourseCapacity, Long> {
    
    Optional<LecturerCourseCapacity> findByLecturerIdAndIsActiveTrue(Long lecturerId);
    
    List<LecturerCourseCapacity> findByDepartmentIdAndIsActiveTrueOrderByLecturerIdAsc(Long departmentId);
}