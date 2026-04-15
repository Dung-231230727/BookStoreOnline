package com.bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderDetailId implements Serializable {
    private String orderId;
    private String isbn;

    public OrderDetailId() {}

    public OrderDetailId(String orderId, String isbn) {
        this.orderId = orderId;
        this.isbn = isbn;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, isbn);
    }
}
