package com.bookstore.service;

import com.bookstore.dto.AdminOrderRequest;
import com.bookstore.dto.CheckoutRequest;
import com.bookstore.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDTO checkout(CheckoutRequest request);
    Page<OrderResponseDTO> getOrderHistory(String username, Pageable pageable);
    OrderResponseDTO getOrderDetail(String orderId);
    void cancelOrder(String orderId);
    
    // Admin / Staff methods
    OrderResponseDTO createAdminOrder(AdminOrderRequest request);
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);
    void updateOrderStatus(String orderId, String status);
}
