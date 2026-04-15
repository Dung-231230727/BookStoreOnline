package com.bookstore.dto;

import java.util.List;

public class AdminOrderRequest {
    private String customerId;
    private String shippingAddress;
    private String paymentMethod;
    private String notes;
    private List<AdminOrderItemRequest> orderDetails;

    public AdminOrderRequest() {}

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<AdminOrderItemRequest> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<AdminOrderItemRequest> orderDetails) { this.orderDetails = orderDetails; }

    public static class AdminOrderItemRequest {
        private String isbn;
        private Integer quantity;
        private Double unitPrice;

        public AdminOrderItemRequest() {}

        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    }
}
