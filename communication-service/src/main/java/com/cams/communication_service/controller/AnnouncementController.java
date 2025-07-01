package com.cams.communication_service.controller;

import com.cams.communication_service.dto.*;
import com.cams.communication_service.model.Announcement;
import com.cams.communication_service.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/com/announcements")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;

    // Create announcement (for admins)
    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @RequestBody CreateAnnouncementRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String createdBy,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        // For demo purposes, using createdBy as createdByName. In real implementation,
        // you might want to fetch the actual name from user service
        String createdByName = "Admin " + createdBy; // This should be fetched from user service
        
        AnnouncementResponse announcement = announcementService.createAnnouncement(
            request, createdBy, createdByName, role, departmentCode);
        return ResponseEntity.ok(announcement);
    }

    // Get announcements for user with stats (for students and lecturers)
    @GetMapping
    public ResponseEntity<AnnouncementStatsResponse> getAnnouncementsForUser(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        AnnouncementStatsResponse response = announcementService.getAnnouncementsForUser(userId, role, departmentCode);
        return ResponseEntity.ok(response);
    }

    // Get announcements by category (for students and lecturers)
    @GetMapping("/category/{category}")
    public ResponseEntity<AnnouncementStatsResponse> getAnnouncementsByCategory(
            @PathVariable Announcement.Category category,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        AnnouncementStatsResponse response = announcementService.getAnnouncementsByCategory(
            userId, role, departmentCode, category);
        return ResponseEntity.ok(response);
    }

    // Get unread announcement count (for students and lecturers)
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadAnnouncementCount(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        int unreadCount = announcementService.getUnreadAnnouncementCount(userId, role, departmentCode);
        return ResponseEntity.ok(unreadCount);
    }

    // Mark announcement as read (for students and lecturers)
    @PostMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAnnouncementAsRead(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        announcementService.markAnnouncementAsRead(id, userId, role);
        return ResponseEntity.ok().build();
    }

    // Get all announcements (for admins)
    @GetMapping("/admin")
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncementsForAdmin(
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<AnnouncementResponse> announcements = announcementService.getAllAnnouncementsForAdmin();
        return ResponseEntity.ok(announcements);
    }

    // Get my announcements (for admins)
    @GetMapping("/my-announcements")
    public ResponseEntity<List<AnnouncementResponse>> getMyAnnouncements(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String createdBy) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<AnnouncementResponse> announcements = announcementService.getMyAnnouncements(createdBy);
        return ResponseEntity.ok(announcements);
    }

    // Update announcement (for admins)
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody UpdateAnnouncementRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String updatedBy,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        AnnouncementResponse announcement = announcementService.updateAnnouncement(
            id, request, updatedBy, role, departmentCode);
        return ResponseEntity.ok(announcement);
    }

    // Delete announcement (for admins)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Department") String departmentCode,
            @RequestHeader("X-User-Id") String createdBy) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        announcementService.deleteAnnouncement(id, role, departmentCode, createdBy);
        return ResponseEntity.noContent().build();
    }

    // Get specific announcement details
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId) {
        
        String userIdForRead = ("STUDENT".equals(role) || "LECTURER".equals(role)) ? userId : null;
        String userRoleForRead = ("STUDENT".equals(role) || "LECTURER".equals(role)) ? role : null;
        
        AnnouncementResponse announcement = announcementService.getAnnouncementById(id, userIdForRead, userRoleForRead);
        return ResponseEntity.ok(announcement);
    }
}