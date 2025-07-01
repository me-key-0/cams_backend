package com.cams.grade_service.repository;

import com.cams.grade_service.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByGroupIdOrderByJoinedAtAsc(Long groupId);
    
    List<GroupMember> findByStudentId(Long studentId);
    
    Optional<GroupMember> findByGroupIdAndStudentId(Long groupId, Long studentId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.courseSessionId = :courseSessionId AND gm.studentId = :studentId")
    List<GroupMember> findByStudentAndCourseSession(@Param("studentId") Long studentId, @Param("courseSessionId") Long courseSessionId);
    
    void deleteByGroupIdAndStudentId(Long groupId, Long studentId);
}