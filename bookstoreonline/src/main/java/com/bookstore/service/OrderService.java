package com.bookstore.service;

import com.bookstore.dto.AdminOrderRequest;
import com.bookstore.dto.CheckoutRequest;
import com.bookstore.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO checkout(CheckoutRequest request);
    List<OrderResponseDTO> getOrderHistory(String username);
    OrderResponseDTO getOrderDetail(String orderId);
    void cancelOrder(String orderId);
    
    // Admin / Staff methods
    OrderResponseDTO createAdminOrder(AdminOrderRequest request);
    List<OrderResponseDTO> getAllOrders();
    void updateOrderStatus(String orderId, String status);
}
