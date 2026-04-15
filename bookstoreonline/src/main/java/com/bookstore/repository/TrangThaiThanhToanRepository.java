package com.bookstore.repository;

import com.bookstore.entity.TrangThaiThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrangThaiThanhToanRepository extends JpaRepository<TrangThaiThanhToan, String> {
}
