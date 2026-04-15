package com.bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class PurchaseOrderDetailId implements Serializable {
    private String purchaseOrderId;
    private String isbn;

    public PurchaseOrderDetailId() {}

    public PurchaseOrderDetailId(String purchaseOrderId, String isbn) {
        this.purchaseOrderId = purchaseOrderId;
        this.isbn = isbn;
    }

    public String getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(String purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderDetailId that = (PurchaseOrderDetailId) o;
        return Objects.equals(purchaseOrderId, that.purchaseOrderId) && Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderId, isbn);
    }
}
