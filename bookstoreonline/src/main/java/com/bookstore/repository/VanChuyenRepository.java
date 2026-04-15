package com.bookstore.repository;

import com.bookstore.entity.VanChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VanChuyenRepository extends JpaRepository<VanChuyen, String> {
    Optional<VanChuyen> findByDonHang_MaDonHang(String maDonHang);
}
