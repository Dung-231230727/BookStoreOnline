package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chi_tiet_phieu_xuat")
public class ChiTietPhieuXuat {
    @EmbeddedId
    private ChiTietPhieuXuatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieuxuat", nullable = false, insertable = false, updatable = false)
    private PhieuXuat phieuXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", nullable = false, insertable = false, updatable = false)
    private Sach sach;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    public ChiTietPhieuXuat() {}

    public ChiTietPhieuXuat(ChiTietPhieuXuatId id, Integer soLuong) {
        this.id = id;
        this.soLuong = soLuong;
    }

    public ChiTietPhieuXuatId getId() { return id; }
    public void setId(ChiTietPhieuXuatId id) { this.id = id; }

    public PhieuXuat getPhieuXuat() { return phieuXuat; }
    public void setPhieuXuat(PhieuXuat phieuXuat) { this.phieuXuat = phieuXuat; }

    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}
