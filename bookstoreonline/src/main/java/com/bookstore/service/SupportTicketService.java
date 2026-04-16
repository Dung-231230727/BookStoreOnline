package com.bookstore.service;

import com.bookstore.dto.SupportMessageDTO;
import com.bookstore.dto.SupportTicketDTO;
import java.util.List;

public interface SupportTicketService {
    List<SupportTicketDTO> getAllTickets();
    List<SupportTicketDTO> getTicketsByCustomer(String username);
    void submitTicket(String username, String subject, String content);
    void updateStatus(Long id, String status);
    void respondToTicket(Long id, String reply, String internalNote, String statusCode);
    List<SupportTicketDTO> getActiveSessions();
    
    // New methods for chat messages
    List<SupportMessageDTO> getMessages(Long ticketId);
    void addMessage(Long ticketId, String senderName, boolean isStaff, String content);
    List<com.bookstore.dto.SupportMessageDTO> getRecentCustomerMessages(java.time.LocalDateTime since);
    List<com.bookstore.dto.SupportMessageDTO> getRecentStaffMessagesForUser(java.time.LocalDateTime since, String username);
    long getUnreadCountForUser(String username);
    long getTotalUnreadCountForStaff();
    void markMessagesAsRead(Long ticketId, boolean forStaff);
}
