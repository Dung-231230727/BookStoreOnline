package com.bookstore.dto;

public class ExportOrderRequestDTO {
    private String orderId;
    private String staffId;

    public ExportOrderRequestDTO() {}

    public ExportOrderRequestDTO(String orderId, String staffId) {
        this.orderId = orderId;
        this.staffId = staffId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}