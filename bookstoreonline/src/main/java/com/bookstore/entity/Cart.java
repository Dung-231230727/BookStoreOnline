package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "carts")
public class Cart {
    @EmbeddedId
    private CartId id = new CartId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn", nullable = false)
    private Book book;

    @Column(name = "quantity")
    private Integer quantity = 1;

    public Cart() {}

    public CartId getId() { return id; }
    public void setId(CartId id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
