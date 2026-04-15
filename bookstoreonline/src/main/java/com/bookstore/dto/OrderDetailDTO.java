package com.bookstore.dto;

import java.math.BigDecimal;

public class OrderDetailDTO {
    private String isbn;
    private String title;
    private Integer quantity;
    private BigDecimal finalPrice;

    public OrderDetailDTO() {}

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
}
