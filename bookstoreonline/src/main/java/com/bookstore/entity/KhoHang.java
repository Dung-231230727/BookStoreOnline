package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kho_hang")
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

    public KhoHang() {}

    public Integer getMaKho() { return maKho; }
    public void setMaKho(Integer maKho) { this.maKho = maKho; }
    public Sach getSach() { return sach; }
    public void setSach(Sach sach) { this.sach = sach; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getViTriKe() { return viTriKe; }
    public void setViTriKe(String viTriKe) { this.viTriKe = viTriKe; }
    public Integer getNguongBaoDong() { return nguongBaoDong; }
    public void setNguongBaoDong(Integer nguongBaoDong) { this.nguongBaoDong = nguongBaoDong; }
}
