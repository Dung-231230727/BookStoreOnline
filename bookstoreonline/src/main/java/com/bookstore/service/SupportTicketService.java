package com.bookstore.service;

import com.bookstore.dto.SupportTicketDTO;
import java.util.List;

public interface SupportTicketService {
    List<SupportTicketDTO> getAllTickets();
    List<SupportTicketDTO> getTicketsByCustomer(String username);
    void submitTicket(String username, String subject, String content);
    void updateStatus(Long id, String status);
}
