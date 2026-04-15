package com.bookstore.repository;

import com.bookstore.entity.PurchaseOrderDetail;
import com.bookstore.entity.PurchaseOrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, PurchaseOrderDetailId> {
}