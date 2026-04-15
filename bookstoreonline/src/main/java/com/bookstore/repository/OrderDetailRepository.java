package com.bookstore.repository;

import com.bookstore.entity.OrderDetail;
import com.bookstore.entity.OrderDetailId;
import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {

    @Query("SELECT od.id.isbn, SUM(od.quantity) as totalSold FROM OrderDetail od GROUP BY od.id.isbn ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProjected();
    
    List<OrderDetail> findByOrder(Order order);
}
