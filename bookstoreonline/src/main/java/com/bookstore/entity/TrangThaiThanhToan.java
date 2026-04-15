package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trang_thai_thanh_toan")
public class TrangThaiThanhToan {
    @Id
    @Column(name = "ma_trang_thai", length = 50)
    private String maTrangThai;

    public TrangThaiThanhToan() {}

    public TrangThaiThanhToan(String maTrangThai) {
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
        return "TrangThaiThanhToan{" +
                "maTrangThai='" + maTrangThai + '\'' +
                '}';
    }
}
