package com.cams.communication_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderRole; // STUDENT or LECTURER

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private boolean isDelivered;

    public enum MessageType {
        TEXT, SYSTEM, TYPING_INDICATOR
    }

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        if (isDelivered == false) {
            isDelivered = false;
        }
        if (isRead == false) {
            isRead = false;
        }
    }
}