package com.bookstore.service;

import com.bookstore.dto.SupportMessageDTO;
import com.bookstore.dto.SupportTicketDTO;
import com.bookstore.entity.SupportMessage;
import com.bookstore.entity.SupportTicket;
import com.bookstore.entity.Customer;
import com.bookstore.repository.SupportMessageRepository;
import com.bookstore.repository.SupportTicketRepository;
import com.bookstore.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final CustomerRepository customerRepository;
    private final SupportMessageRepository supportMessageRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, 
                                   CustomerRepository customerRepository,
                                   SupportMessageRepository supportMessageRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.customerRepository = customerRepository;
        this.supportMessageRepository = supportMessageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getAllTickets() {
        return supportTicketRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getTicketsByCustomer(String username) {
        return supportTicketRepository.findByCustomer_Account_Username(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void submitTicket(String username, String title, String content) {
        System.out.println("DEBUG: Submitting ticket for user: " + username);
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Customer not found for username: " + username));
        
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(customer);
        ticket.setTitle(title);
        ticket.setContent(content);
        ticket = supportTicketRepository.save(ticket);
        System.out.println("DEBUG: Ticket saved with ID: " + ticket.getTicketId());

        // Also add the first message
        addMessage(ticket.getTicketId(), customer.getFullName(), false, content);
        System.out.println("DEBUG: Initial message added to ticket.");
    }

    @Override
    public void updateStatus(Long id, String statusCode) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatusCode(statusCode);
        supportTicketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void respondToTicket(Long id, String reply, String internalNote, String statusCode) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (reply != null && !reply.trim().isEmpty()) {
            ticket.setAdminReply(reply);
            // Add as a chat message
            addMessage(id, "Admin", true, reply);
        }
        if (internalNote != null) ticket.setInternalNote(internalNote);
        if (statusCode != null) ticket.setStatusCode(statusCode);
        
        supportTicketRepository.save(ticket);
    }

    @Override
    public List<SupportMessageDTO> getMessages(Long ticketId) {
        return supportMessageRepository.findByTicket_TicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(SupportMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addMessage(Long ticketId, String senderName, boolean isStaff, String content) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        SupportMessage message = new SupportMessage();
        message.setTicket(ticket);
        message.setSenderName(senderName);
        message.setStaff(isStaff);
        message.setContent(content);
        supportMessageRepository.save(message);
        
        // If it's a customer message, set status to OPEN or PROCESSING if it was RESOLVED/CLOSED
        if (!isStaff && (ticket.getStatusCode().equals("RESOLVED") || ticket.getStatusCode().equals("CLOSED"))) {
            ticket.setStatusCode("PROCESSING");
            supportTicketRepository.save(ticket);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportMessageDTO> getRecentCustomerMessages(java.time.LocalDateTime since) {
        return supportMessageRepository.findByCreatedAtAfterAndIsStaffFalse(since).stream()
                .map(SupportMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportMessageDTO> getRecentStaffMessagesForUser(java.time.LocalDateTime since, String username) {
        return supportMessageRepository.findByCreatedAtAfterAndIsStaffTrueAndTicket_Customer_Account_Username(since, username).stream()
                .map(SupportMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketDTO> getActiveSessions() {
        return supportTicketRepository.findByStatusCodeIn(List.of("OPEN", "PROCESSING", "ai-chat")).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCountForUser(String username) {
        return supportMessageRepository.countByTicket_Customer_Account_UsernameAndIsStaffTrueAndIsReadFalse(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUnreadCountForStaff() {
        return supportMessageRepository.countByIsStaffFalseAndIsReadFalse();
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long ticketId, boolean forStaff) {
        List<SupportMessage> unread = supportMessageRepository.findByTicket_TicketIdAndIsStaffAndIsReadFalse(ticketId, !forStaff);
        unread.forEach(m -> m.setRead(true));
        supportMessageRepository.saveAll(unread);
    }

    private SupportTicketDTO toDTO(SupportTicket ticket) {
        SupportTicketDTO dto = new SupportTicketDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setCustomerName(ticket.getCustomer().getFullName());
        dto.setTitle(ticket.getTitle());
        dto.setContent(ticket.getContent());
        dto.setStatusCode(ticket.getStatusCode());
        dto.setAdminReply(ticket.getAdminReply());
        dto.setInternalNote(ticket.getInternalNote());
        dto.setCreatedAt(ticket.getCreatedAt());
        
        // Determine unread status: latest message is not from staff
        List<SupportMessage> messages = supportMessageRepository.findByTicket_TicketIdOrderByCreatedAtAsc(ticket.getTicketId());
        if (!messages.isEmpty()) {
            SupportMessage last = messages.get(messages.size() - 1);
            dto.setHasUnreadMessages(!last.isStaff());
        } else {
            dto.setHasUnreadMessages(true); // Content from ticket creator
        }
        
        return dto;
    }
}
