package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sach_vat_ly")
@PrimaryKeyJoinColumn(name = "isbn")
public class SachVatLy extends Sach {
    @Column(name = "can_nang", precision = 5, scale = 2)
    private BigDecimal canNang;

    public SachVatLy() {}

    public BigDecimal getCanNang() { return canNang; }
    public void setCanNang(BigDecimal canNang) { this.canNang = canNang; }
}
