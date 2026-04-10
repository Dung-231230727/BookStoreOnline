package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "sach_dien_tu")
@PrimaryKeyJoinColumn(name = "isbn")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SachDienTu extends Sach {

    @Column(name = "dung_luong_file", precision = 10, scale = 2)
    private BigDecimal dungLuongFile;

    @Column(name = "duong_dan_tai", length = 255)
    private String duongDanTai;
}
