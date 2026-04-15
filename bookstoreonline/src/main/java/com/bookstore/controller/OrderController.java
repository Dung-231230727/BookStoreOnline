package com.bookstore.controller;

import com.bookstore.dto.AdminOrderRequest;
import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.CheckoutRequest;
import com.bookstore.dto.OrderResponseDTO;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Quản lý đơn hàng và thanh toán")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Xử lý giao dịch đặt hàng", description = "Tạo đơn hàng mới từ giỏ hàng hiện tại")
    public ApiResponse<OrderResponseDTO> checkout(@RequestBody CheckoutRequest request) {
        return ApiResponse.success("Đặt hàng thành công", orderService.checkout(request));
    }

    @GetMapping("/history")
    @Operation(summary = "Xem lịch sử đơn của khách", description = "Lấy danh sách đơn hàng đã đặt của người dùng hiện tại")
    public ApiResponse<List<OrderResponseDTO>> getHistory(@RequestParam String username) {
        return ApiResponse.success(orderService.getOrderHistory(username));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết hóa đơn", description = "Lấy thông tin chi tiết của một đơn hàng cụ thể")
    public ApiResponse<OrderResponseDTO> getOrderDetail(@PathVariable String id) {
        return ApiResponse.success(orderService.getOrderDetail(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Khách hàng tự hủy đơn", description = "Hủy đơn hàng nếu đơn vẫn ở trạng thái chờ xác nhận hoặc chờ thanh toán")
    public ApiResponse<String> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);
        return ApiResponse.success("Đã hủy đơn hàng thành công", null);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "[STAFF] Quản lý đơn hàng", description = "Admin xem toàn bộ danh sách đơn hàng trong hệ thống")
    public ApiResponse<List<OrderResponseDTO>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "[STAFF] Cập nhật trạng thái đơn", description = "Admin cập nhật trạng thái mới cho đơn hàng (Xác nhận, Đã giao, v.v)")
    public ApiResponse<String> updateStatus(@PathVariable String id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ApiResponse.success("Cập nhật trạng thái thành công", null);
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "[STAFF] Tạo đơn hàng mới", description = "Nhân viên tạo đơn hàng trực tiếp cho khách hàng")
    public ApiResponse<OrderResponseDTO> createAdminOrder(@RequestBody AdminOrderRequest request) {
        return ApiResponse.success("Tạo đơn hàng thành công", orderService.createAdminOrder(request));
    }
}
