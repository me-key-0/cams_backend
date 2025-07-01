package com.cams.communication_service.dto;

import com.cams.communication_service.model.Notification;
import com.cams.communication_service.model.NotificationRead;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDtoConverter {
    
    public static NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setSubject(notification.getSubject());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setCourseSessionId(notification.getCourseSessionId());
        response.setLecturerId(notification.getLecturerId());
        response.setLecturerName(notification.getLecturerName());
        response.setCreatedAt(notification.getCreatedAt());
        response.setActive(notification.isActive());
        response.setRead(false); // Default, will be set based on student's read status
        return response;
    }

    public static NotificationResponse toResponseWithReadStatus(Notification notification, String studentId) {
        NotificationResponse response = toResponse(notification);
        
        // Check if student has read this notification
        boolean isRead = notification.getReadStatuses() != null && 
                        notification.getReadStatuses().stream()
                            .anyMatch(readStatus -> readStatus.getStudentId().equals(studentId));
        
        response.setRead(isRead);
        return response;
    }

    public static List<NotificationResponse> toResponseList(List<Notification> notifications) {
        return notifications.stream()
            .map(NotificationDtoConverter::toResponse)
            .collect(Collectors.toList());
    }

    public static List<NotificationResponse> toResponseListWithReadStatus(List<Notification> notifications, String studentId) {
        return notifications.stream()
            .map(notification -> toResponseWithReadStatus(notification, studentId))
            .collect(Collectors.toList());
    }
}