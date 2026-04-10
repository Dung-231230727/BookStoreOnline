package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ChiTietPhieuNhapId implements Serializable {
    private String phieuNhap;
    private String sach;
}

@Entity
@Table(name = "chi_tiet_phieu_nhap")
@IdClass(ChiTietPhieuNhapId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieunhap")
    private PhieuNhap phieuNhap;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private Sach sach;

    @Column(nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia_nhap", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGiaNhap;
}
