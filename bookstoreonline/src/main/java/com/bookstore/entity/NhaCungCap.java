package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nha_cung_cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ncc")
    private Integer maNcc;

    @Column(name = "ten_ncc", nullable = false, length = 150)
    private String tenNcc;

    @Column(name = "thong_tin_lien_he", columnDefinition = "NVARCHAR(MAX)")
    private String thongTinLienHe;
}
