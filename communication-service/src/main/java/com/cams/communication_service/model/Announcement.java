package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String role; // ADMIN or SUPER_ADMIN

    @Column(nullable = true)
    private String departmentCode; // Only for department-specific announcements

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean isGlobal = false; // true for SUPER_ADMIN announcements, false for department-specific announcements
}
