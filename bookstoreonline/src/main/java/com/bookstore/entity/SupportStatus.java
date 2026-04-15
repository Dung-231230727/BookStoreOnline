package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "support_status")
public class SupportStatus {
    @Id
    @Column(name = "status_code", length = 50)
    private String statusCode;

    public SupportStatus() {}

    public SupportStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "SupportStatus{" +
                "statusCode='" + statusCode + '\'' +
                '}';
    }
}
