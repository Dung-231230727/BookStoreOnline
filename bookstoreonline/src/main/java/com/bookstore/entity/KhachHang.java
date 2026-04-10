package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "khach_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khachhang")
    private Long maKhachHang;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", unique = true, nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(length = 15)
    private String sdt;

    @Column(name = "dia_chi_giao_hang", columnDefinition = "NVARCHAR(MAX)")
    private String diaChiGiaoHang;

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy = 0;
}
