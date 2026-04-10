package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_xuat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuat {

    @Id
    @Column(name = "ma_phieuxuat", length = 20)
    private String maPhieuXuat;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang", unique = true, nullable = false)
    private DonHang donHang;

    @Column(name = "ngay_xuat")
    private LocalDateTime ngayXuat = LocalDateTime.now();
}
