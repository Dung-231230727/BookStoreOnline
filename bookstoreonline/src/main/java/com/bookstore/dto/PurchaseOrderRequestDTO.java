package com.bookstore.dto;

import java.util.List;

public class PurchaseOrderRequestDTO {
    private Integer supplierId;
    private Integer staffId;
    private List<PurchaseOrderDetailRequest> details;

    public PurchaseOrderRequestDTO() {}

    public PurchaseOrderRequestDTO(Integer supplierId, Integer staffId, List<PurchaseOrderDetailRequest> details) {
        this.supplierId = supplierId;
        this.staffId = staffId;
        this.details = details;
    }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public List<PurchaseOrderDetailRequest> getDetails() { return details; }
    public void setDetails(List<PurchaseOrderDetailRequest> details) { this.details = details; }
}