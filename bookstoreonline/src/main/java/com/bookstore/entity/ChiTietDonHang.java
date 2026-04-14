package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {
    @EmbeddedId
    private ChiTietDonHangId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maDonHang")
    @JoinColumn(name = "ma_donhang")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    @JoinColumn(name = "isbn")
    private Sach sach;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "gia_ban_chot", precision = 12, scale = 2, nullable = false)
    private BigDecimal giaBanChot;

    public ChiTietDonHang() {}

    public ChiTietDonHangId getId() { return id; }
    public void setId(ChiTietDonHangId id) { this.id = id; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGiaBanChot() { return giaBanChot; }
    public void setGiaBanChot(BigDecimal giaBanChot) { this.giaBanChot = giaBanChot; }
}
