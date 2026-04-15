package com.bookstore.dto;

import java.math.BigDecimal;

public class PurchaseOrderResponseDTO {
    private String purchaseOrderId;
    private BigDecimal totalAmount;
    private String message;

    public PurchaseOrderResponseDTO() {}

    public PurchaseOrderResponseDTO(String purchaseOrderId, BigDecimal totalAmount, String message) {
        this.purchaseOrderId = purchaseOrderId;
        this.totalAmount = totalAmount;
        this.message = message;
    }

    public String getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(String purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}