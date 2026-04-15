package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trang_thai_don_hang")
public class TrangThaiDonHang {
    @Id
    @Column(name = "ma_trang_thai", length = 50)
    private String maTrangThai;

    public TrangThaiDonHang() {}

    public TrangThaiDonHang(String maTrangThai) {
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
        return "TrangThaiDonHang{" +
                "maTrangThai='" + maTrangThai + '\'' +
                '}';
    }
}
