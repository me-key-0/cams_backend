package com.cams.communication_service.service;

import com.cams.communication_service.dto.AnnouncementDtoConverter;
import com.cams.communication_service.dto.AnnouncementResponse;
import com.cams.communication_service.dto.CreateAnnouncementRequest;
import com.cams.communication_service.model.Announcement;
import com.cams.communication_service.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {
    
    @Autowired
    private AnnouncementRepository announcementRepository;

    @Transactional
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request, String createdBy, String role, String departmentCode) {
        if (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN")) {
            throw new IllegalArgumentException("Only ADMIN and SUPER_ADMIN can create announcements");
        }
        
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setCreatedBy(createdBy);
        announcement.setRole(role);
        
        // Set department and global flags based on role
        if (role.equals("SUPER_ADMIN")) {
            announcement.setGlobal(true);
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

    public List<AnnouncementResponse> getAnnouncementsForUser(String role, String departmentCode) {
        List<Announcement> announcements = getAnnouncementsInternal(role, departmentCode);
        return AnnouncementDtoConverter.toResponseList(announcements);
    }

    private List<Announcement> getAnnouncementsInternal(String role, String departmentCode) {
        if (role.equals("STUDENT") || role.equals("LECTURER")) {
            // For students and lecturers, get both global announcements and department-specific ones
            List<Announcement> globalAnnouncements = announcementRepository.findByActiveTrueAndIsGlobalTrueOrderByCreatedAtDesc();
            List<Announcement> departmentAnnouncements = announcementRepository.findByActiveTrueAndDepartmentCodeOrderByCreatedAtDesc(departmentCode);
            
            // Combine both lists and sort by creation time
            List<Announcement> allAnnouncements = new ArrayList<>();
            allAnnouncements.addAll(globalAnnouncements);
            allAnnouncements.addAll(departmentAnnouncements);
            
            return allAnnouncements.stream()
                .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
                .collect(Collectors.toList());
        }
        
        return announcementRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public void deleteAnnouncement(Long id, String role, String departmentCode, String createdBy) {
        if (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN")) {
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
}
