package com.cams.communication_service.repository;

import com.cams.communication_service.model.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    
    // Check if a student has read a specific notification
    Optional<NotificationRead> findByNotificationIdAndStudentId(Long notificationId, String studentId);
    
    // Get all read notifications for a student
    List<NotificationRead> findByStudentId(String studentId);
    
    // Count unread notifications for a student in specific course sessions
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.courseSessionId IN :courseSessionIds AND n.active = true " +
           "AND n.id NOT IN (SELECT nr.notification.id FROM NotificationRead nr WHERE nr.studentId = :studentId)")
    long countUnreadNotificationsForStudent(@Param("studentId") String studentId, 
                                          @Param("courseSessionIds") List<Long> courseSessionIds);
}