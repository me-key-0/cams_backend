package com.cams.course_service.repository;

import com.cams.course_service.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
    List<CourseSession> findBySemester(String semester);
}