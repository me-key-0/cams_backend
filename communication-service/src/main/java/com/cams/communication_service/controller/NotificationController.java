package com.cams.communication_service.controller;

import com.cams.communication_service.dto.CreateNotificationRequest;
import com.cams.communication_service.dto.NotificationResponse;
import com.cams.communication_service.dto.NotificationStatsResponse;
import com.cams.communication_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/com/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    // Create notification (for lecturers)
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody CreateNotificationRequest request,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        // For demo purposes, using lecturerId as lecturerName. In real implementation,
        // you might want to fetch the actual name from user service
        String lecturerName = "Lecturer " + lecturerId; // This should be fetched from user service
        
        NotificationResponse notification = notificationService.createNotification(request, lecturerId, lecturerName);
        return ResponseEntity.ok(notification);
    }

    // Get notifications created by lecturer (for lecturers)
    @GetMapping("/my-notifications")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByLecturer(lecturerId);
        return ResponseEntity.ok(notifications);
    }

    // Get notifications for student with stats (for students)
    @GetMapping("/student")
    public ResponseEntity<NotificationStatsResponse> getNotificationsForStudent(
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        NotificationStatsResponse response = notificationService.getNotificationsForStudent(studentId);
        return ResponseEntity.ok(response);
    }

    // Get unread notification count (for students)
    @GetMapping("/student/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationCount(
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        int unreadCount = notificationService.getUnreadNotificationCount(studentId);
        return ResponseEntity.ok(unreadCount);
    }

    // Get notifications by course session (for students)
    @GetMapping("/course-session/{courseSessionId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByCourseSession(courseSessionId, studentId);
        return ResponseEntity.ok(notifications);
    }

    // Get notifications by type and course session (for students)
    @GetMapping("/course-session/{courseSessionId}/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable Long courseSessionId,
            @PathVariable String type,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByType(courseSessionId, type, studentId);
        return ResponseEntity.ok(notifications);
    }

    // Mark notification as read (for students)
    @PostMapping("/{notificationId}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        notificationService.markNotificationAsRead(notificationId, studentId);
        return ResponseEntity.ok().build();
    }

    // Delete notification (for lecturers)
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        notificationService.deleteNotification(notificationId, lecturerId);
        return ResponseEntity.noContent().build();
    }

    // Get specific notification details
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Long notificationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        String studentId = "STUDENT".equals(role) ? userId : null;
        NotificationResponse notification = notificationService.getNotificationById(notificationId, studentId);
        return ResponseEntity.ok(notification);
    }
}