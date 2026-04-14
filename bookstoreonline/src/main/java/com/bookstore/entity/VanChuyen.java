package com.bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "van_chuyen")
public class VanChuyen {
    @Id
    @Column(name = "ma_vanchuyen", length = 30)
    private String maVanChuyen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang", unique = true, nullable = false)
    private DonHang donHang;

    @Column(length = 50)
    private String doiTac = "GHN";

    @Column(name = "tracking_id", length = 50)
    private String trackingId;

    @Column(name = "trang_thai_tracking", length = 100)
    private String trangThaiTracking;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat = LocalDateTime.now();

    public VanChuyen() {}

    public String getMaVanChuyen() { return maVanChuyen; }
    public void setMaVanChuyen(String maVanChuyen) { this.maVanChuyen = maVanChuyen; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public String getDoiTac() { return doiTac; }
    public void setDoiTac(String doiTac) { this.doiTac = doiTac; }
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getTrangThaiTracking() { return trangThaiTracking; }
    public void setTrangThaiTracking(String trangThaiTracking) { this.trangThaiTracking = trangThaiTracking; }
    public LocalDateTime getThoiGianCapNhat() { return thoiGianCapNhat; }
    public void setThoiGianCapNhat(LocalDateTime thoiGianCapNhat) { this.thoiGianCapNhat = thoiGianCapNhat; }
}
