package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "nha_cung_cap")
public class NhaCungCap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ncc")
    private Integer maNcc;

    @Column(name = "ten_ncc", nullable = false, length = 150)
    private String tenNcc;

    @Column(name = "thong_tin_lien_he", columnDefinition = "NVARCHAR(MAX)")
    private String thongTinLienHe;

    public NhaCungCap() {}

    public Integer getMaNcc() { return maNcc; }
    public void setMaNcc(Integer maNcc) { this.maNcc = maNcc; }
    public String getTenNcc() { return tenNcc; }
    public void setTenNcc(String tenNcc) { this.tenNcc = tenNcc; }
    public String getThongTinLienHe() { return thongTinLienHe; }
    public void setThongTinLienHe(String thongTinLienHe) { this.thongTinLienHe = thongTinLienHe; }
}
