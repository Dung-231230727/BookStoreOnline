package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_status")
public class OrderStatus {
    @Id
    @Column(name = "status_code", length = 50)
    private String statusCode;

    public OrderStatus() {}

    public OrderStatus(String statusCode) {
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
        return "OrderStatus{" +
                "statusCode='" + statusCode + '\'' +
                '}';
    }
}
