package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sach_dien_tu")
@PrimaryKeyJoinColumn(name = "isbn")
public class SachDienTu extends Sach {
    @Column(name = "dung_luong_file", precision = 10, scale = 2)
    private BigDecimal dungLuongFile;

    @Column(name = "duong_dan_tai", length = 255)
    private String duongDanTai;

    public SachDienTu() {}

    public BigDecimal getDungLuongFile() { return dungLuongFile; }
    public void setDungLuongFile(BigDecimal dungLuongFile) { this.dungLuongFile = dungLuongFile; }
    public String getDuongDanTai() { return duongDanTai; }
    public void setDuongDanTai(String duongDanTai) { this.duongDanTai = duongDanTai; }
}
