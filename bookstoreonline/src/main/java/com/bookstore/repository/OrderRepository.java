package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT COALESCE(SUM(o.totalPayment), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    org.springframework.data.domain.Page<Order> findAllByCustomer_Account_UsernameOrderByCreatedAtDesc(String username, org.springframework.data.domain.Pageable pageable);
}
