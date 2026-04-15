package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.TrackingResponseDTO;
import com.bookstore.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@Tag(name = "Shipping Management", description = "Theo dõi và cập nhật trạng thái vận chuyển")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/track/{id}")
    @Operation(summary = "Theo dõi vận chuyển (ID vận đơn)")
    public ResponseEntity<ApiResponse<TrackingResponseDTO>> trackOrder(@PathVariable String id) {
        TrackingResponseDTO response = shippingService.trackOrder(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/tracking/{orderId}")
    @Operation(summary = "Theo dõi vận chuyển (Mã đơn hàng)")
    public ResponseEntity<ApiResponse<TrackingResponseDTO>> getTrackingByOrderId(@PathVariable String orderId) {
        TrackingResponseDTO response = shippingService.trackOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái vận chuyển")
    public ResponseEntity<ApiResponse<String>> updateShippingStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        
        shippingService.updateShippingStatus(id, status, notes);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật trạng thái vận chuyển thành công", "OK"));
    }
}
