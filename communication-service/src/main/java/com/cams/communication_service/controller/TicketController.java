package com.cams.communication_service.controller;

import com.cams.communication_service.dto.CreateTicketRequest;
import com.cams.communication_service.dto.CreateTicketResponseRequest;
import com.cams.communication_service.dto.TicketResponse;
import com.cams.communication_service.model.Ticket;
import com.cams.communication_service.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/com/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;

    // Create a new ticket (for students and lecturers)
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @RequestBody CreateTicketRequest request,
            @RequestHeader("X-User-Id") String senderId,
            @RequestHeader("X-User-Role") String senderRole,
            @RequestHeader("X-User-Department") String departmentCode) {
        
        // For demo purposes, using senderId as senderName. In real implementation,
        // you might want to fetch the actual name from user service
        String senderName = "User " + senderId; // This should be fetched from user service
        
        TicketResponse ticket = ticketService.createTicket(request, senderId, senderRole, senderName, departmentCode);
        return ResponseEntity.ok(ticket);
    }

    // Get tickets for the current user (students/lecturers see their own tickets)
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @RequestHeader("X-User-Id") String senderId) {
        List<TicketResponse> tickets = ticketService.getTicketsBySender(senderId);
        return ResponseEntity.ok(tickets);
    }

    // Get all tickets (for admins)
    @GetMapping("/admin")
    public ResponseEntity<List<TicketResponse>> getAllTicketsForAdmin(
            @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        List<TicketResponse> tickets = ticketService.getAllTicketsForAdmin();
        return ResponseEntity.ok(tickets);
    }

    // Get filtered tickets (for admins)
    @GetMapping("/admin/filtered")
    public ResponseEntity<List<TicketResponse>> getFilteredTickets(
            @RequestHeader("X-User-Role") String role,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) String senderRole,
            @RequestParam(required = false) Ticket.Status status,
            @RequestParam(required = false) Boolean isRead) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<TicketResponse> tickets = ticketService.getFilteredTickets(priority, senderRole, status, isRead);
        return ResponseEntity.ok(tickets);
    }

    // Respond to a ticket (for admins)
    @PostMapping("/{ticketId}/respond")
    public ResponseEntity<TicketResponse> respondToTicket(
            @PathVariable Long ticketId,
            @RequestBody CreateTicketResponseRequest request,
            @RequestHeader("X-User-Id") String responderId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        // For demo purposes, using responderId as responderName
        String responderName = "Admin " + responderId; // This should be fetched from user service
        
        TicketResponse ticket = ticketService.respondToTicket(ticketId, request, responderId, responderName);
        return ResponseEntity.ok(ticket);
    }

    // Mark ticket as read (for admins)
    @PutMapping("/{ticketId}/mark-read")
    public ResponseEntity<Void> markTicketAsRead(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        ticketService.markTicketAsRead(ticketId);
        return ResponseEntity.ok().build();
    }

    // Update ticket status (for admins)
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam Ticket.Status status,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        TicketResponse ticket = ticketService.updateTicketStatus(ticketId, status);
        return ResponseEntity.ok(ticket);
    }

    // Delete ticket (for admins)
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }

    // Get specific ticket details
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") String userId) {
        
        TicketResponse ticket = ticketService.getTicketById(ticketId);
        
        // Check permissions: admins can see all tickets, users can only see their own
        if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
            if (!ticket.getSenderId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }
        }
        
        return ResponseEntity.ok(ticket);
    }
}