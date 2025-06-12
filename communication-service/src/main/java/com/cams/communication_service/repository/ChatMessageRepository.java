package com.cams.communication_service.repository;

import com.cams.communication_service.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findByRoomIdOrderBySentAtDesc(@Param("roomId") String roomId, Pageable pageable);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId ORDER BY cm.sentAt ASC")
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(@Param("roomId") String roomId);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId AND cm.senderId != :userId AND cm.isRead = false")
    int countUnreadMessagesInRoom(@Param("roomId") String roomId, @Param("userId") Long userId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId ORDER BY cm.sentAt DESC LIMIT 1")
    ChatMessage findLastMessageInRoom(@Param("roomId") String roomId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.roomId = :roomId AND cm.senderId != :userId AND cm.isRead = false")
    List<ChatMessage> findUnreadMessagesInRoom(@Param("roomId") String roomId, @Param("userId") Long userId);
}