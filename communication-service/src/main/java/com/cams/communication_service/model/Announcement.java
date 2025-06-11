package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String createdByName; // Name of the admin who created it

    @Column(nullable = false)
    private String role; // ADMIN or SUPER_ADMIN

    @Column(nullable = true)
    private String departmentCode; // Only for department-specific announcements

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean isGlobal = false; // true for SUPER_ADMIN announcements, false for department-specific announcements

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementRead> readStatuses;

    public enum Category {
        ACADEMIC, ADMINISTRATIVE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}