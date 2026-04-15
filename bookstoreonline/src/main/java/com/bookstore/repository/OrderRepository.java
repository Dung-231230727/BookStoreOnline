package com.bookstore.repository;

import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT COALESCE(SUM(o.totalPayment), 0) FROM Order o WHERE o.statusCode = :statusCode")
    BigDecimal sumTotalAmountByStatusCode(@Param("statusCode") String statusCode);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statusCode = :statusCode")
    long countByStatusCode(@Param("statusCode") String statusCode);

    List<Order> findAllByCustomer_Account_UsernameOrderByCreatedAtDesc(String username);
}
