package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification_reads")
public class NotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private LocalDateTime readAt;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
}