package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {

    @Id
    @Column(name = "ma_thanhtoan", length = 50)
    private String maThanhToan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang", nullable = false)
    private DonHang donHang;

    @Column(name = "phuong_thuc", nullable = false)
    private String phuongThuc; // COD, VNPAY, MOMO

    @Column(name = "trang_thai")
    private String trangThai = "PENDING"; // SUCCESS, FAILED

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "ma_tham_chieu_cong", length = 100)
    private String maThamChieuCong;
}
