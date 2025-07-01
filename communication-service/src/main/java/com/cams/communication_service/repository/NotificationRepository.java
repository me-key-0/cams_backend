package com.cams.communication_service.repository;

import com.cams.communication_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by course session ID (for students to see notifications in their courses)
    List<Notification> findByCourseSessionIdAndActiveTrueOrderByCreatedAtDesc(Long courseSessionId);
    
    // Find notifications by lecturer ID (for lecturers to see their own notifications)
    List<Notification> findByLecturerIdAndActiveTrueOrderByCreatedAtDesc(String lecturerId);
    
    // Find notifications by multiple course session IDs (for students enrolled in multiple courses)
    @Query("SELECT n FROM Notification n WHERE n.courseSessionId IN :courseSessionIds AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findByCourseSessionIdsAndActiveTrue(@Param("courseSessionIds") List<Long> courseSessionIds);
    
    // Find notifications by type and course session
    List<Notification> findByCourseSessionIdAndTypeAndActiveTrueOrderByCreatedAtDesc(Long courseSessionId, String type);
}