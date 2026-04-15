package com.bookstore.repository;

import com.bookstore.entity.ChiTietPhieuXuat;
import com.bookstore.entity.ChiTietPhieuXuatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietPhieuXuatRepository extends JpaRepository<ChiTietPhieuXuat, ChiTietPhieuXuatId> {
}
