package com.cams.communication_service.dto;

import com.cams.communication_service.model.Announcement;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementDtoConverter {
    
    public static AnnouncementResponse toResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setContent(announcement.getContent());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setCreatedBy(announcement.getCreatedBy());
        response.setRole(announcement.getRole());
        response.setDepartmentCode(announcement.getDepartmentCode());
        response.setGlobal(announcement.isGlobal());
        return response;
    }

    public static List<AnnouncementResponse> toResponseList(List<Announcement> announcements) {
        return announcements.stream()
            .map(AnnouncementDtoConverter::toResponse)
            .collect(Collectors.toList());
    }
}
