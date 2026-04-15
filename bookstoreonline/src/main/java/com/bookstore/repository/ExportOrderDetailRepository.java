package com.bookstore.repository;

import com.bookstore.entity.ExportOrderDetail;
import com.bookstore.entity.ExportOrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportOrderDetailRepository extends JpaRepository<ExportOrderDetail, ExportOrderDetailId> {
}
