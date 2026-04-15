package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietPhieuXuatId implements Serializable {
    @Column(name = "ma_phieuxuat", length = 20)
    private String maPhieuXuat;

    @Column(name = "isbn", length = 13)
    private String isbn;

    public ChiTietPhieuXuatId() {}

    public ChiTietPhieuXuatId(String maPhieuXuat, String isbn) {
        this.maPhieuXuat = maPhieuXuat;
        this.isbn = isbn;
    }

    public String getMaPhieuXuat() { return maPhieuXuat; }
    public void setMaPhieuXuat(String maPhieuXuat) { this.maPhieuXuat = maPhieuXuat; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuXuatId that = (ChiTietPhieuXuatId) o;
        return Objects.equals(maPhieuXuat, that.maPhieuXuat) &&
               Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuXuat, isbn);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuXuatId{" +
                "maPhieuXuat='" + maPhieuXuat + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
