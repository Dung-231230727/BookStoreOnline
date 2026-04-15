package com.bookstore.repository;

import com.bookstore.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
    
    // Find vouchers that are still valid
    List<Voucher> findByExpiryDateAfter(LocalDateTime date);
}
