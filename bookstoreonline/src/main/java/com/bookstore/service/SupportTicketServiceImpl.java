package com.bookstore.service;

import com.bookstore.dto.SupportTicketDTO;
import com.bookstore.entity.SupportTicket;
import com.bookstore.entity.Customer;
import com.bookstore.enums.SupportStatus;
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

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, CustomerRepository customerRepository) {
        this.supportTicketRepository = supportTicketRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<SupportTicketDTO> getAllTickets() {
        return supportTicketRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicketDTO> getTicketsByCustomer(String username) {
        return supportTicketRepository.findByCustomer_Account_Username(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submitTicket(String username, String title, String content) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(customer);
        ticket.setTitle(title);
        ticket.setContent(content);
        supportTicketRepository.save(ticket);
    }

    @Override
    public void updateStatus(Long id, String statusName) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(SupportStatus.valueOf(statusName.toUpperCase()));
        supportTicketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void respondToTicket(Long id, String reply, String internalNote, SupportStatus status) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (reply != null) ticket.setAdminReply(reply);
        if (internalNote != null) ticket.setInternalNote(internalNote);
        if (status != null) {
            ticket.setStatus(status);
        }

        supportTicketRepository.save(ticket);
    }

    private SupportTicketDTO toDTO(SupportTicket ticket) {
        SupportTicketDTO dto = new SupportTicketDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setCustomerName(ticket.getCustomer().getFullName());
        dto.setTitle(ticket.getTitle());
        dto.setContent(ticket.getContent());
        dto.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : null);
        dto.setAdminReply(ticket.getAdminReply());
        dto.setInternalNote(ticket.getInternalNote());
        dto.setCreatedAt(ticket.getCreatedAt());
        return dto;
    }
}
