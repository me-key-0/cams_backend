package com.cams.communication_service.repository;

import com.cams.communication_service.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByRoomId(String roomId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.courseSessionId = :courseSessionId AND cr.studentId = :studentId AND cr.lecturerId = :lecturerId")
    Optional<ChatRoom> findByCourseSessionAndParticipants(@Param("courseSessionId") Long courseSessionId,
                                                          @Param("studentId") Long studentId,
                                                          @Param("lecturerId") Long lecturerId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.studentId = :studentId AND cr.isActive = true ORDER BY cr.lastActivity DESC")
    List<ChatRoom> findByStudentIdAndActiveOrderByLastActivity(@Param("studentId") Long studentId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.lecturerId = :lecturerId AND cr.isActive = true ORDER BY cr.lastActivity DESC")
    List<ChatRoom> findByLecturerIdAndActiveOrderByLastActivity(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.courseSessionId = :courseSessionId AND cr.lecturerId = :lecturerId AND cr.isActive = true ORDER BY cr.lastActivity DESC")
    List<ChatRoom> findByCourseSessionAndLecturerOrderByLastActivity(@Param("courseSessionId") Long courseSessionId,
                                                                    @Param("lecturerId") Long lecturerId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.courseSessionId IN :courseSessionIds AND cr.studentId = :studentId AND cr.isActive = true ORDER BY cr.lastActivity DESC")
    List<ChatRoom> findByStudentAndCourseSessionsOrderByLastActivity(@Param("studentId") Long studentId,
                                                                    @Param("courseSessionIds") List<Long> courseSessionIds);
}