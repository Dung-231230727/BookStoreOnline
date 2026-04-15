package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.ExportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportOrderRepository extends JpaRepository<ExportOrder, String> {
    boolean existsByOrder(Order order);
}