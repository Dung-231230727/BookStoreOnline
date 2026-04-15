package com.bookstore.dto;

public class SupplierDTO {
    private Integer supplierId;
    private String supplierName;
    private String contactInfo;

    public SupplierDTO() {}

    public SupplierDTO(Integer supplierId, String supplierName, String contactInfo) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
    }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}