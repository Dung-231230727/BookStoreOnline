package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_status")
public class PaymentStatus {
    @Id
    @Column(name = "status_code", length = 50)
    private String statusCode;

    public PaymentStatus() {}

    public PaymentStatus(String statusCode) {
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
        return "PaymentStatus{" +
                "statusCode='" + statusCode + '\'' +
                '}';
    }
}
