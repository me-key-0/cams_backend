package com.cams.communication_service.dto;

import com.cams.communication_service.model.Announcement;
import com.cams.communication_service.model.AnnouncementRead;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementDtoConverter {
    
    public static AnnouncementResponse toResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setContent(announcement.getContent());
        response.setCategory(announcement.getCategory());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setCreatedBy(announcement.getCreatedBy());
        response.setCreatedByName(announcement.getCreatedByName());
        response.setRole(announcement.getRole());
        response.setDepartmentCode(announcement.getDepartmentCode());
        response.setGlobal(announcement.isGlobal());
        response.setActive(announcement.isActive());
        response.setRead(false); // Default, will be set based on user's read status
        return response;
    }

    public static AnnouncementResponse toResponseWithReadStatus(Announcement announcement, String userId, String userRole) {
        AnnouncementResponse response = toResponse(announcement);
        
        // Check if user has read this announcement
        boolean isRead = announcement.getReadStatuses() != null && 
                        announcement.getReadStatuses().stream()
                            .anyMatch(readStatus -> readStatus.getUserId().equals(userId) && 
                                                  readStatus.getUserRole().equals(userRole));
        
        response.setRead(isRead);
        return response;
    }

    public static List<AnnouncementResponse> toResponseList(List<Announcement> announcements) {
        return announcements.stream()
            .map(AnnouncementDtoConverter::toResponse)
            .collect(Collectors.toList());
    }

    public static List<AnnouncementResponse> toResponseListWithReadStatus(List<Announcement> announcements, String userId, String userRole) {
        return announcements.stream()
            .map(announcement -> toResponseWithReadStatus(announcement, userId, userRole))
            .collect(Collectors.toList());
    }
}