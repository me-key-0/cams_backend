package com.cams.communication_service.repository;

import com.cams.communication_service.model.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoom.roomId = :roomId")
    List<ChatParticipant> findByRoomId(@Param("roomId") String roomId);
    
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoom.roomId = :roomId AND cp.userId = :userId")
    Optional<ChatParticipant> findByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") Long userId);
    
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId")
    List<ChatParticipant> findByUserId(@Param("userId") Long userId);
}