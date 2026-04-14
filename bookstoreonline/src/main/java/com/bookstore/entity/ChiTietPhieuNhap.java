package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_phieu_nhap")
public class ChiTietPhieuNhap {
    @EmbeddedId
    private ChiTietPhieuNhapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPhieuNhap")
    @JoinColumn(name = "ma_phieunhap")
    private PhieuNhap phieuNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn")
    private Sach sach;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia_nhap", precision = 12, scale = 2, nullable = false)
    private BigDecimal donGiaNhap;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhapId getId() { return id; }
    public void setId(ChiTietPhieuNhapId id) { this.id = id; }
    public PhieuNhap getPhieuNhap() { return phieuNhap; }
    public void setPhieuNhap(PhieuNhap phieuNhap) { this.phieuNhap = phieuNhap; }
    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(BigDecimal donGiaNhap) { this.donGiaNhap = donGiaNhap; }
}
