package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {

    @Id
    @Column(name = "ma_donhang", length = 20)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khachhang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_voucher")
    private Voucher voucher;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "tong_tien_hang", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTienHang;

    @Column(name = "phi_vanchuyen", precision = 10, scale = 2)
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan", nullable = false, precision = 15, scale = 2)
    private BigDecimal tongThanhToan;

    @Column(name = "trang_thai")
    private String trangThai = "MOI"; // MOI, DA_XAC_NHAN, ...

    @Column(name = "dia_chi_giao_cu_the", columnDefinition = "NVARCHAR(MAX)")
    private String diaChiGiaoCuThe;
}
