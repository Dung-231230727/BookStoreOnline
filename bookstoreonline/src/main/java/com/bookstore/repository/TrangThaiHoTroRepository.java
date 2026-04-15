package com.bookstore.repository;

import com.bookstore.entity.TrangThaiHoTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrangThaiHoTroRepository extends JpaRepository<TrangThaiHoTro, String> {
}
