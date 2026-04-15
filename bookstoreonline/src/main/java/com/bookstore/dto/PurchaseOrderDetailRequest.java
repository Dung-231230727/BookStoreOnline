package com.bookstore.dto;

import java.math.BigDecimal;

public class PurchaseOrderDetailRequest {
    private String isbn;
    private Integer quantity;
    private BigDecimal unitPrice;

    public PurchaseOrderDetailRequest() {}

    public PurchaseOrderDetailRequest(String isbn, Integer quantity, BigDecimal unitPrice) {
        this.isbn = isbn;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}