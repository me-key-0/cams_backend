package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type; // deadline, reminder, or custom type

    @Column(nullable = false)
    private Long courseSessionId;

    @Column(nullable = false)
    private String lecturerId; // ID of lecturer who posted

    @Column(nullable = false)
    private String lecturerName; // Name of lecturer

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationRead> readStatuses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}