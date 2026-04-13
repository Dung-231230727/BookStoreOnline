package com.bookstore.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietPhieuNhapId implements Serializable {
    private String maPhieuNhap;
    private String isbn;

    public ChiTietPhieuNhapId() {}

    public ChiTietPhieuNhapId(String maPhieuNhap, String isbn) {
        this.maPhieuNhap = maPhieuNhap;
        this.isbn = isbn;
    }

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhapId that = (ChiTietPhieuNhapId) o;
        return Objects.equals(maPhieuNhap, that.maPhieuNhap) && Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap, isbn);
    }
}
