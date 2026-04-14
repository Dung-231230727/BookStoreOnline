package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
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

    @Column(name = "tong_tien_hang", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTienHang;

    @Column(name = "phi_vanchuyen", precision = 10, scale = 2)
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongThanhToan;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "MOI";

    @Column(name = "dia_chi_giao_cu_the", columnDefinition = "NVARCHAR(MAX)")
    private String diaChiGiaoCuThe;

    public DonHang() {}

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public BigDecimal getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(BigDecimal tongTienHang) { this.tongTienHang = tongTienHang; }
    public BigDecimal getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(BigDecimal phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }
    public BigDecimal getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(BigDecimal tongThanhToan) { this.tongThanhToan = tongThanhToan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getDiaChiGiaoCuThe() { return diaChiGiaoCuThe; }
    public void setDiaChiGiaoCuThe(String diaChiGiaoCuThe) { this.diaChiGiaoCuThe = diaChiGiaoCuThe; }
}
