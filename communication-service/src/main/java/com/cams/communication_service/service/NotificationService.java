package com.cams.communication_service.service;

import com.cams.communication_service.client.CourseServiceClient;
import com.cams.communication_service.client.CourseSessionDto;
import com.cams.communication_service.dto.CreateNotificationRequest;
import com.cams.communication_service.dto.NotificationDtoConverter;
import com.cams.communication_service.dto.NotificationResponse;
import com.cams.communication_service.dto.NotificationStatsResponse;
import com.cams.communication_service.model.Notification;
import com.cams.communication_service.model.NotificationRead;
import com.cams.communication_service.repository.NotificationRepository;
import com.cams.communication_service.repository.NotificationReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationReadRepository notificationReadRepository;
    
    @Autowired
    private CourseServiceClient courseServiceClient;

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request, String lecturerId, String lecturerName) {
        // Validate that lecturer is assigned to the course session
        boolean isAuthorized = courseServiceClient.validateLecturerForCourseSession(
            Long.parseLong(lecturerId), request.getCourseSessionId());
        
        // if (!isAuthorized) {
        //     throw new IllegalArgumentException("Lecturer is not authorized to post notifications for this course session");
        // }
        
        Notification notification = new Notification();
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setCourseSessionId(request.getCourseSessionId());
        notification.setLecturerId(lecturerId);
        notification.setLecturerName(lecturerName);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActive(true);
        
        Notification savedNotification = notificationRepository.save(notification);
        return NotificationDtoConverter.toResponse(savedNotification);
    }

    public List<NotificationResponse> getNotificationsByLecturer(String lecturerId) {
        List<Notification> notifications = notificationRepository.findByLecturerIdAndActiveTrueOrderByCreatedAtDesc(lecturerId);
        return NotificationDtoConverter.toResponseList(notifications);
    }

    public NotificationStatsResponse getNotificationsForStudent(String studentId) {
        // Get student's course sessions
        List<CourseSessionDto> courseSessions = courseServiceClient.getCourseSessionByStudentId(Long.parseLong(studentId));
        List<Long> courseSessionIds = courseSessions.stream()
            .map(CourseSessionDto::getId)
            .collect(Collectors.toList());
        
        if (courseSessionIds.isEmpty()) {
            NotificationStatsResponse response = new NotificationStatsResponse();
            response.setTotalNotifications(0);
            response.setUnreadCount(0);
            response.setNotifications(List.of());
            return response;
        }
        
        // Get notifications for student's course sessions
        List<Notification> notifications = notificationRepository.findByCourseSessionIdsAndActiveTrue(courseSessionIds);
        
        // Convert to response with read status
        List<NotificationResponse> notificationResponses = 
            NotificationDtoConverter.toResponseListWithReadStatus(notifications, studentId);
        
        // Count unread notifications
        long unreadCount = notificationReadRepository.countUnreadNotificationsForStudent(studentId, courseSessionIds);
        
        NotificationStatsResponse response = new NotificationStatsResponse();
        response.setTotalNotifications(notifications.size());
        response.setUnreadCount((int) unreadCount);
        response.setNotifications(notificationResponses);
        
        return response;
    }

    public List<NotificationResponse> getNotificationsByCourseSession(Long courseSessionId, String studentId) {
        List<Notification> notifications = notificationRepository.findByCourseSessionIdAndActiveTrueOrderByCreatedAtDesc(courseSessionId);
        return NotificationDtoConverter.toResponseListWithReadStatus(notifications, studentId);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, String studentId) {
        // Check if already read
        if (notificationReadRepository.findByNotificationIdAndStudentId(notificationId, studentId).isPresent()) {
            return; // Already marked as read
        }
        
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        NotificationRead notificationRead = new NotificationRead();
        notificationRead.setNotification(notification);
        notificationRead.setStudentId(studentId);
        notificationRead.setReadAt(LocalDateTime.now());
        
        notificationReadRepository.save(notificationRead);
    }

    @Transactional
    public void deleteNotification(Long notificationId, String lecturerId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        // Verify that the lecturer owns this notification
        if (!notification.getLecturerId().equals(lecturerId)) {
            throw new IllegalArgumentException("You can only delete your own notifications");
        }
        
        // Soft delete by setting active to false
        notification.setActive(false);
        notificationRepository.save(notification);
    }

    public NotificationResponse getNotificationById(Long notificationId, String studentId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        if (studentId != null) {
            return NotificationDtoConverter.toResponseWithReadStatus(notification, studentId);
        } else {
            return NotificationDtoConverter.toResponse(notification);
        }
    }

    public int getUnreadNotificationCount(String studentId) {
        // Get student's course sessions
        List<CourseSessionDto> courseSessions = courseServiceClient.getCourseSessionByStudentId(Long.parseLong(studentId));
        List<Long> courseSessionIds = courseSessions.stream()
            .map(CourseSessionDto::getId)
            .collect(Collectors.toList());
        
        if (courseSessionIds.isEmpty()) {
            return 0;
        }
        
        return (int) notificationReadRepository.countUnreadNotificationsForStudent(studentId, courseSessionIds);
    }

    public List<NotificationResponse> getNotificationsByType(Long courseSessionId, String type, String studentId) {
        List<Notification> notifications = notificationRepository.findByCourseSessionIdAndTypeAndActiveTrueOrderByCreatedAtDesc(courseSessionId, type);
        return NotificationDtoConverter.toResponseListWithReadStatus(notifications, studentId);
    }
}