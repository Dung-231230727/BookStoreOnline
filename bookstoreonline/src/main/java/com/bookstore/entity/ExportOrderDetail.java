package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "export_order_details")
public class ExportOrderDetail {
    @EmbeddedId
    private ExportOrderDetailId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exportOrderId")
    @JoinColumn(name = "export_order_id", nullable = false)
    private ExportOrder exportOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn", nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public ExportOrderDetail() {}

    public ExportOrderDetail(ExportOrderDetailId id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public ExportOrderDetailId getId() { return id; }
    public void setId(ExportOrderDetailId id) { this.id = id; }

    public ExportOrder getExportOrder() { return exportOrder; }
    public void setExportOrder(ExportOrder exportOrder) { this.exportOrder = exportOrder; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
