package com.bookstore.dto;

import com.bookstore.entity.AuditLog;
import java.time.LocalDateTime;

public class AuditLogDTO {
    private Long logId;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    public AuditLogDTO() {}

    public AuditLogDTO(AuditLog log) {
        if (log != null) {
            this.logId = log.getLogId();
            this.username = log.getAccount() != null ? log.getAccount().getUsername() : null;
            this.action = log.getAction();
            this.details = log.getDetails();
            this.timestamp = log.getTimestamp();
        }
    }

    public AuditLogDTO(Long logId, String username, String action, String details, LocalDateTime timestamp) {
        this.logId = logId;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
