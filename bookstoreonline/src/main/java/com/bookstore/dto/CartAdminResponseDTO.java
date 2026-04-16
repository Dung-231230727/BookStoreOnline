package com.bookstore.dto;

import java.math.BigDecimal;

public class CartAdminResponseDTO {
    private String username;
    private Long customerId;
    private String bookTitle;
    private String isbn;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public CartAdminResponseDTO(String username, Long customerId, String bookTitle, String isbn, Integer quantity, BigDecimal unitPrice) {
        this.username = username;
        this.customerId = customerId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(new BigDecimal(quantity));
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
