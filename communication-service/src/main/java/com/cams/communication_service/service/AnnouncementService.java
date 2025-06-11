package com.cams.communication_service.service;

import com.cams.communication_service.dto.*;
import com.cams.communication_service.model.Announcement;
import com.cams.communication_service.model.AnnouncementRead;
import com.cams.communication_service.repository.AnnouncementRepository;
import com.cams.communication_service.repository.AnnouncementReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private AnnouncementReadRepository announcementReadRepository;

    @Transactional
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request, String createdBy, 
                                                 String createdByName, String role, String departmentCode) {
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            throw new IllegalArgumentException("Only ADMIN and SUPER_ADMIN can create announcements");
        }
        
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCategory(request.getCategory());
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setCreatedBy(createdBy);
        announcement.setCreatedByName(createdByName);
        announcement.setRole(role);
        announcement.setActive(true);
        
        // Set department and global flags based on role
        if ("SUPER_ADMIN".equals(role)) {
            announcement.setGlobal(true);
            announcement.setDepartmentCode(null); // Global announcements don't have department
        } else {
            if (departmentCode == null || departmentCode.isEmpty()) {
                throw new IllegalArgumentException("Department code is required for department announcements");
            }
            announcement.setDepartmentCode(departmentCode);
            announcement.setGlobal(false);
        }
        
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return AnnouncementDtoConverter.toResponse(savedAnnouncement);
    }

    public AnnouncementStatsResponse getAnnouncementsForUser(String userId, String userRole, String departmentCode) {
        List<Announcement> announcements = announcementRepository.findAnnouncementsForUser(departmentCode);
        
        // Convert to response with read status
        List<AnnouncementResponse> announcementResponses = 
            AnnouncementDtoConverter.toResponseListWithReadStatus(announcements, userId, userRole);
        
        // Count unread announcements
        long unreadCount = announcementReadRepository.countUnreadAnnouncementsForUser(userId, userRole, departmentCode);
        
        AnnouncementStatsResponse response = new AnnouncementStatsResponse();
        response.setTotalAnnouncements(announcements.size());
        response.setUnreadCount((int) unreadCount);
        response.setAnnouncements(announcementResponses);
        
        return response;
    }

    public AnnouncementStatsResponse getAnnouncementsByCategory(String userId, String userRole, 
                                                              String departmentCode, Announcement.Category category) {
        List<Announcement> announcements = announcementRepository.findAnnouncementsForUserByCategory(departmentCode, category);
        
        // Convert to response with read status
        List<AnnouncementResponse> announcementResponses = 
            AnnouncementDtoConverter.toResponseListWithReadStatus(announcements, userId, userRole);
        
        // Count unread announcements by category
        long unreadCount = announcementReadRepository.countUnreadAnnouncementsForUserByCategory(
            userId, userRole, departmentCode, category);
        
        AnnouncementStatsResponse response = new AnnouncementStatsResponse();
        response.setTotalAnnouncements(announcements.size());
        response.setUnreadCount((int) unreadCount);
        response.setAnnouncements(announcementResponses);
        
        return response;
    }

    public List<AnnouncementResponse> getAllAnnouncementsForAdmin() {
        List<Announcement> announcements = announcementRepository.findByActiveTrueOrderByCreatedAtDesc();
        return AnnouncementDtoConverter.toResponseList(announcements);
    }

    public List<AnnouncementResponse> getMyAnnouncements(String createdBy) {
        List<Announcement> announcements = announcementRepository.findByCreatedByAndActiveTrueOrderByCreatedAtDesc(createdBy);
        return AnnouncementDtoConverter.toResponseList(announcements);
    }

    @Transactional
    public void markAnnouncementAsRead(Long announcementId, String userId, String userRole) {
        // Check if already read
        if (announcementReadRepository.findByAnnouncementIdAndUserIdAndUserRole(announcementId, userId, userRole).isPresent()) {
            return; // Already marked as read
        }
        
        Announcement announcement = announcementRepository.findById(announcementId)
            .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        
        AnnouncementRead announcementRead = new AnnouncementRead();
        announcementRead.setAnnouncement(announcement);
        announcementRead.setUserId(userId);
        announcementRead.setUserRole(userRole);
        announcementRead.setReadAt(LocalDateTime.now());
        
        announcementReadRepository.save(announcementRead);
    }

    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, UpdateAnnouncementRequest request, 
                                                 String updatedBy, String role, String departmentCode) {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        
        // Check if the user trying to update is the creator of the announcement
        if (!announcement.getCreatedBy().equals(updatedBy)) {
            throw new IllegalArgumentException("Only the creator can update their own announcements");
        }
        
        // Only allow updating department announcements if the department matches
        if (!announcement.isGlobal() && !announcement.getDepartmentCode().equals(departmentCode)) {
            throw new IllegalArgumentException("Cannot update announcements from other departments");
        }
        
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCategory(request.getCategory());
        
        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return AnnouncementDtoConverter.toResponse(updatedAnnouncement);
    }

    @Transactional
    public void deleteAnnouncement(Long id, String role, String departmentCode, String createdBy) {
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            throw new IllegalArgumentException("Only ADMIN and SUPER_ADMIN can delete announcements");
        }
        
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        
        // Check if the user trying to delete is the creator of the announcement
        if (!announcement.getCreatedBy().equals(createdBy)) {
            throw new IllegalArgumentException("Only the creator can delete their own announcements");
        }
        
        // Only allow deleting department announcements if the department matches
        if (!announcement.isGlobal() && !announcement.getDepartmentCode().equals(departmentCode)) {
            throw new IllegalArgumentException("Cannot delete announcements from other departments");
        }
        
        announcement.setActive(false);
        announcementRepository.save(announcement);
    }

    public AnnouncementResponse getAnnouncementById(Long id, String userId, String userRole) {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        
        if (userId != null && userRole != null) {
            return AnnouncementDtoConverter.toResponseWithReadStatus(announcement, userId, userRole);
        } else {
            return AnnouncementDtoConverter.toResponse(announcement);
        }
    }

    public int getUnreadAnnouncementCount(String userId, String userRole, String departmentCode) {
        return (int) announcementReadRepository.countUnreadAnnouncementsForUser(userId, userRole, departmentCode);
    }
}