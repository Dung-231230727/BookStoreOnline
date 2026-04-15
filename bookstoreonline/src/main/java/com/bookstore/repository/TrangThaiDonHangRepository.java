package com.bookstore.repository;

import com.bookstore.entity.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, String> {
}
