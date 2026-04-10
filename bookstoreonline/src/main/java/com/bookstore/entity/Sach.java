package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "sach")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sach {

    @Id
    @Column(length = 13)
    private String isbn;

    @Column(name = "ten_sach", nullable = false, length = 255)
    private String tenSach;

    @Column(name = "gia_niem_yet", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaNiemYet;

    @Column(name = "so_trang")
    private Integer soTrang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_danhmuc")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nxb")
    private Nxb nxb;

    @Column(name = "mo_ta_ngu_nghia", columnDefinition = "NVARCHAR(MAX)")
    private String moTaNguNghia; // Metadata cho AI Search

    @Column(name = "anh_bia")
    private String anhBia;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sach_tac_gia",
        joinColumns = @JoinColumn(name = "isbn"),
        inverseJoinColumns = @JoinColumn(name = "ma_tacgia")
    )
    private Set<TacGia> danhSachTacGia;
}
