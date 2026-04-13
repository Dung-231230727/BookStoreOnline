package com.bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
public class ThanhToan {
    @Id
    @Column(name = "ma_thanhtoan", length = 50)
    private String maThanhToan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang", nullable = false)
    private DonHang donHang;

    @Column(name = "phuong_thuc", nullable = false, length = 50)
    private String phuongThuc;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "PENDING";

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "ma_tham_chieu_cong", length = 100)
    private String maThamChieuCong;

    public ThanhToan() {}

    public String getMaThanhToan() { return maThanhToan; }
    public void setMaThanhToan(String maThanhToan) { this.maThanhToan = maThanhToan; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public String getMaThamChieuCong() { return maThamChieuCong; }
    public void setMaThamChieuCong(String maThamChieuCong) { this.maThamChieuCong = maThamChieuCong; }
}
