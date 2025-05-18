package com.cams.course_service.repository;

import com.cams.course_service.model.CourseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {
//     @Query("SELECT cs.id FROM CourseSession cs WHERE cs.year = :year AND cs.semester = :semester AND cs.academicYear = :academicYear")
// List<Long> findCourseSessionIdsByYearAndSemesterAndAcademicYear(@Param("year") Integer year, 
//                                                                 @Param("semester") Integer semester, 
//                                                                 @Param("academicYear") Integer academicYear);


// List<CourseSession> findByStudentId(Long studentId);

}