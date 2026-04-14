package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khachhang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", nullable = false)
    private Sach sach;

    @Column(name = "so_luong")
    private Integer soLuong = 1;

    public GioHang() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}
