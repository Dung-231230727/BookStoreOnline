package com.bookstore.service;

import com.bookstore.dto.AuditLogDTO;
import com.bookstore.entity.AuditLog;
import com.bookstore.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> filterLogs(String username, String actionKeyword, LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc();
        
        return logs.stream()
                .filter(log -> username == null || log.getAccount().getUsername().toLowerCase().contains(username.toLowerCase()))
                .filter(log -> actionKeyword == null || log.getAction().toLowerCase().contains(actionKeyword.toLowerCase()))
                .filter(log -> startDate == null || !log.getTimestamp().isBefore(startDate))
                .filter(log -> endDate == null || !log.getTimestamp().isAfter(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AuditLogDTO> getLogById(Long id) {
        if (id == null) return Optional.empty();
        return auditLogRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public void log(com.bookstore.entity.Account account, String action, String details) {
        AuditLog log = new AuditLog();
        log.setAccount(account);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    private AuditLogDTO convertToDTO(AuditLog log) {
        return new AuditLogDTO(
                log.getLogId(),
                log.getAccount().getUsername(),
                log.getAction(),
                log.getDetails(),
                log.getTimestamp()
        );
    }
}
