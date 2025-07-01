package com.cams.grade_service.repository;

import com.cams.grade_service.model.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    
    List<StudentGrade> findByStudentIdAndGradeType_CourseSessionId(Long studentId, Long courseSessionId);
    
    List<StudentGrade> findByGradeType_CourseSessionIdOrderByStudentNameAsc(Long courseSessionId);
    
    Optional<StudentGrade> findByStudentIdAndGradeTypeId(Long studentId, Long gradeTypeId);
    
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.gradeType.courseSessionId = :courseSessionId AND sg.gradeType.id = :gradeTypeId")
    List<StudentGrade> findByGradeTypeAndCourseSession(@Param("gradeTypeId") Long gradeTypeId, @Param("courseSessionId") Long courseSessionId);
    
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.groupId = :groupId AND sg.gradeType.id = :gradeTypeId")
    List<StudentGrade> findByGroupIdAndGradeTypeId(@Param("groupId") Long groupId, @Param("gradeTypeId") Long gradeTypeId);
    
    void deleteByStudentIdAndGradeTypeId(Long studentId, Long gradeTypeId);
}