package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_participants")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userRole; // STUDENT or LECTURER

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime lastSeenAt;

    @Column(nullable = false)
    private boolean isOnline;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        lastSeenAt = LocalDateTime.now();
        if (isOnline == false) {
            isOnline = false;
        }
    }
}