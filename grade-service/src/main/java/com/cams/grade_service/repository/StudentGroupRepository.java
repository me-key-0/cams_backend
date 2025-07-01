package com.cams.grade_service.repository;

import com.cams.grade_service.model.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    
    List<StudentGroup> findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtDesc(Long courseSessionId);
    
    List<StudentGroup> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(Long createdBy);
    
    @Query("SELECT sg FROM StudentGroup sg JOIN sg.members gm WHERE gm.studentId = :studentId AND sg.isActive = true ORDER BY sg.createdAt DESC")
    List<StudentGroup> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT sg FROM StudentGroup sg WHERE sg.courseSessionId = :courseSessionId AND sg.type = :type AND sg.isActive = true ORDER BY sg.createdAt DESC")
    List<StudentGroup> findByCourseSessionAndType(@Param("courseSessionId") Long courseSessionId, @Param("type") StudentGroup.GroupType type);
}