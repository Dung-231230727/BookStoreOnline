package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "nxb")
public class Nxb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nxb")
    private Integer maNxb;

    @Column(name = "ten_nxb", nullable = false, length = 150)
    private String tenNxb;

    public Nxb() {}

    public Integer getMaNxb() { return maNxb; }
    public void setMaNxb(Integer maNxb) { this.maNxb = maNxb; }
    public String getTenNxb() { return tenNxb; }
    public void setTenNxb(String tenNxb) { this.tenNxb = tenNxb; }
}
