package com.bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "export_orders")
public class ExportOrder {
    @Id
    @Column(name = "export_order_id", length = 20)
    private String exportOrderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;

    @Column(name = "export_date")
    private LocalDateTime exportDate = LocalDateTime.now();

    @Column(name = "exported_by")
    private String exportedBy;

    public ExportOrder() {}

    public String getExportOrderId() { return exportOrderId; }
    public void setExportOrderId(String exportOrderId) { this.exportOrderId = exportOrderId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public LocalDateTime getExportDate() { return exportDate; }
    public void setExportDate(LocalDateTime exportDate) { this.exportDate = exportDate; }
    public String getExportedBy() { return exportedBy; }
    public void setExportedBy(String exportedBy) { this.exportedBy = exportedBy; }
}
