package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bookstore.entity.AuditLog;
import java.time.format.DateTimeFormatter;

public class AuditLogDTO {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "admin")
    private String username;

    @Schema(example = "UPDATE_ORDER_STATUS")
    private String action;

    @Schema(example = "Order ORD-1234: NEW -> CONFIRMED")
    private String details;

    @Schema(example = "13/04/2024 08:30:45")
    private String timestamp;

    public AuditLogDTO(AuditLog log) {
        this.id = log.getLogId();
        if (log.getAccount() != null) {
            this.username = log.getAccount().getUsername();
        }
        this.action = log.getAction();
        this.details = log.getDetails();
        if (log.getTimestamp() != null) {
            this.timestamp = log.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getTimestamp() { return timestamp; }
}
