package com.cams.course_service.repository;

import com.cams.course_service.model.CourseSession;
import com.cams.course_service.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseSessionId(Long courseSessionId);
    @Query("""
        SELECT e.courseSession 
        FROM Enrollment e 
        WHERE e.studentId = :studentId 
        AND e.courseSession.year = :year 
        AND e.courseSession.semester = :semester 
        AND e.courseSession.academicYear = :academicYear
    """)
    List<CourseSession> findMatchingCourseSessions(
        @Param("studentId") Long studentId,
        @Param("year") Integer year,
        @Param("semester") Integer semester,
        @Param("academicYear") Integer academicYear
    );

    
}


