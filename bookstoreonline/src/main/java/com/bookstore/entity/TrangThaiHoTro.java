package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trang_thai_ho_tro")
public class TrangThaiHoTro {
    @Id
    @Column(name = "ma_trang_thai", length = 50)
    private String maTrangThai;

    public TrangThaiHoTro() {}

    public TrangThaiHoTro(String maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    public String getMaTrangThai() {
        return maTrangThai;
    }

    public void setMaTrangThai(String maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    @Override
    public String toString() {
        return "TrangThaiHoTro{" +
                "maTrangThai='" + maTrangThai + '\'' +
                '}';
    }
}
