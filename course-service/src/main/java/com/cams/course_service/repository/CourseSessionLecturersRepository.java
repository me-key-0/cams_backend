package com.cams.course_service.repository;

import com.cams.course_service.model.CourseSessionLecturers;
import com.cams.course_service.model.LecturerCourseCapacity;

import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;import java.util.Optional;

@Repository
public interface CourseSessionLecturersRepository extends JpaRepository<CourseSessionLecturers, Long> {
    
    @Query("SELECT c.lecturerId FROM CourseSessionLecturers c WHERE c.courseSessionId = :courseSessionId")
    List<Long> getLecturerIdsByCourseSessionId(@Param("courseSessionId") Long courseSessionId);
}