package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcement_reads")
public class AnnouncementRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // Student or Lecturer ID

    @Column(nullable = false)
    private String userRole; // STUDENT or LECTURER

    @Column(nullable = false)
    private LocalDateTime readAt;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
}