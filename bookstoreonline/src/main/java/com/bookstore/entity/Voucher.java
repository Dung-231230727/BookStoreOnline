package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @Column(name = "ma_voucher", length = 20)
    private String maVoucher;

    @Column(name = "gia_tri_giam", precision = 10, scale = 2, nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "dieu_kien_toi_thieu", precision = 12, scale = 2)
    private BigDecimal dieuKienToiThieu = BigDecimal.ZERO;

    @Column(name = "thoi_han", nullable = false)
    private LocalDateTime thoiHan;

    public Voucher() {}

    public String getMaVoucher() { return maVoucher; }
    public void setMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }
    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }
    public BigDecimal getDieuKienToiThieu() { return dieuKienToiThieu; }
    public void setDieuKienToiThieu(BigDecimal dieuKienToiThieu) { this.dieuKienToiThieu = dieuKienToiThieu; }
    public LocalDateTime getThoiHan() { return thoiHan; }
    public void setThoiHan(LocalDateTime thoiHan) { this.thoiHan = thoiHan; }
}
