package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_nhap")
public class PhieuNhap {
    @Id
    @Column(name = "ma_phieunhap", length = 20)
    private String maPhieuNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ncc", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhanvien", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap = LocalDateTime.now();

    @Column(name = "tong_tien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    public PhieuNhap() {}

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
    public NhaCungCap getNhaCungCap() { return nhaCungCap; }
    public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
}
