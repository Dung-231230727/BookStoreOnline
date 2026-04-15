package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {
    @Id
    @Column(name = "ma_pt", length = 50)
    private String maPt;

    public PhuongThucThanhToan() {}

    public PhuongThucThanhToan(String maPt) {
        this.maPt = maPt;
    }

    public String getMaPt() {
        return maPt;
    }

    public void setMaPt(String maPt) {
        this.maPt = maPt;
    }

    @Override
    public String toString() {
        return "PhuongThucThanhToan{" +
                "maPt='" + maPt + '\'' +
                '}';
    }
}
