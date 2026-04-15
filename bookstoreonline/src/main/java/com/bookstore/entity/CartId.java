package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartId implements Serializable {

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "isbn", length = 13)
    private String isbn;

    public CartId() {}

    public CartId(Long customerId, String isbn) {
        this.customerId = customerId;
        this.isbn = isbn;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartId that = (CartId) o;
        return Objects.equals(customerId, that.customerId) && 
               Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, isbn);
    }
}
