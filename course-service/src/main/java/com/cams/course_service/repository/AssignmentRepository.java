package com.cams.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cams.course_service.model.Assignment;
import com.cams.course_service.model.Assignment.Status;

import java.util.List;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long>{
    List<Assignment> findByLecturerId(Long lecturerId);
    List<Assignment> findByLecturerIdAndStatus(Long lecturerId, Status status);
} 
