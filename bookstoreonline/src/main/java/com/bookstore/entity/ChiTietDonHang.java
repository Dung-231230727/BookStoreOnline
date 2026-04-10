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
class ChiTietDonHangId implements Serializable {
    private String donHang;
    private String sach;
}

@Entity
@Table(name = "chi_tiet_don_hang")
@IdClass(ChiTietDonHangId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHang {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang")
    private DonHang donHang;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private Sach sach;

    @Column(nullable = false)
    private Integer soLuong;

    @Column(name = "gia_ban_chot", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaBanChot;
}
