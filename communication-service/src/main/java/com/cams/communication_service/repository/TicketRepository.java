package com.cams.communication_service.repository;

import com.cams.communication_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Find tickets by sender ID (for students/lecturers to see their own tickets)
    List<Ticket> findBySenderIdOrderByCreatedAtDesc(String senderId);
    
    // Find all tickets ordered by creation date (for admin view)
    List<Ticket> findAllByOrderByCreatedAtDesc();
    
    // Find tickets by priority
    List<Ticket> findByPriorityOrderByCreatedAtDesc(Ticket.Priority priority);
    
    // Find tickets by sender role
    List<Ticket> findBySenderRoleOrderByCreatedAtDesc(String senderRole);
    
    // Find tickets by priority and sender role
    List<Ticket> findByPriorityAndSenderRoleOrderByCreatedAtDesc(Ticket.Priority priority, String senderRole);
    
    // Find unread tickets
    List<Ticket> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Find tickets by status
    List<Ticket> findByStatusOrderByCreatedAtDesc(Ticket.Status status);
    
    // Custom query for filtering tickets with multiple criteria
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:senderRole IS NULL OR t.senderRole = :senderRole) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:isRead IS NULL OR t.isRead = :isRead) " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findTicketsWithFilters(@Param("priority") Ticket.Priority priority,
                                       @Param("senderRole") String senderRole,
                                       @Param("status") Ticket.Status status,
                                       @Param("isRead") Boolean isRead);
}