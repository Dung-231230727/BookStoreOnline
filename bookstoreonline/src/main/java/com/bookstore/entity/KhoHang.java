package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kho_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_kho")
    private Integer maKho;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", unique = true, nullable = false)
    private Sach sach;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon = 0;

    @Column(name = "vi_tri_ke", length = 50)
    private String viTriKe;

    @Column(name = "nguong_bao_dong")
    private Integer nguongBaoDong = 5;
}
