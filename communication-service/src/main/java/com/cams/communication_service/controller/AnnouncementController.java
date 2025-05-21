package com.cams.communication_service.controller;

import com.cams.communication_service.dto.CreateAnnouncementRequest;
import com.cams.communication_service.dto.AnnouncementResponse;
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

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @RequestBody CreateAnnouncementRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String createdBy,
            @RequestHeader("X-User-Department") String departmentCode) {
        return ResponseEntity.ok(announcementService.createAnnouncement(request, createdBy, role, departmentCode));
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements(
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Department") String departmentCode) {
        return ResponseEntity.ok(announcementService.getAnnouncementsForUser(role, departmentCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Department") String departmentCode,
            @RequestHeader("X-User-Id") String createdBy) {
        announcementService.deleteAnnouncement(id, role, departmentCode, createdBy);
        return ResponseEntity.noContent().build();
    }
}
