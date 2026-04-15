package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExportOrderDetailId implements Serializable {
    @Column(name = "export_order_id", length = 20)
    private String exportOrderId;

    @Column(name = "isbn", length = 13)
    private String isbn;

    public ExportOrderDetailId() {}

    public ExportOrderDetailId(String exportOrderId, String isbn) {
        this.exportOrderId = exportOrderId;
        this.isbn = isbn;
    }

    public String getExportOrderId() { return exportOrderId; }
    public void setExportOrderId(String exportOrderId) { this.exportOrderId = exportOrderId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportOrderDetailId that = (ExportOrderDetailId) o;
        return Objects.equals(exportOrderId, that.exportOrderId) &&
               Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exportOrderId, isbn);
    }

    @Override
    public String toString() {
        return "ExportOrderDetailId{" +
                "exportOrderId='" + exportOrderId + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
