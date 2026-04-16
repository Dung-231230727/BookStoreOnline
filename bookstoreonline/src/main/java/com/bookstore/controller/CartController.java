package com.bookstore.controller;

import com.bookstore.dto.CartAdminResponseDTO;
import org.springframework.data.domain.Pageable;
import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.CartDTO;
import com.bookstore.entity.Cart;
import com.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
@Tag(name = "Cart Management", description = "Quản lý giỏ hàng của khách hàng")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Deprecated
    @GetMapping("/admin/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả giỏ hàng (LEGACY - SAFE)", description = "Đã được DTO hóa để tránh LazyLoading. Khuyến nghị dùng /admin/list")
    public ApiResponse<List<CartAdminResponseDTO>> getAllCarts() {
        // Return a list version of the DTOs for backward compatibility
        return ApiResponse.success(cartService.getAllActiveCartsDTO(org.springframework.data.domain.Pageable.unpaged()).getContent());
    }

    @GetMapping("/admin/list")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách giỏ hàng DTO", description = "Admin xem tất cả giỏ hàng (Phân trang)")
    public ApiResponse<java.util.List<CartAdminResponseDTO>> getAdminCartList(Pageable pageable) {
        return ApiResponse.successPage(cartService.getAllActiveCartsDTO(pageable));
    }

    @GetMapping
    @Operation(summary = "Lấy giỏ hàng của tôi", description = "Xem danh sách các sản phẩm trong giỏ hàng của người dùng hiện tại")
    public ApiResponse<List<CartDTO>> getCart(java.security.Principal principal) {
        return ApiResponse.success(cartService.getCart(principal.getName()));
    }

    @PostMapping("/add")
    @Operation(summary = "Thêm vào giỏ", description = "Sử dụng Principal thay vì username param")
    public ApiResponse<String> addToCart(
            java.security.Principal principal,
            @RequestParam String isbn,
            @RequestParam Integer quantity) {
        cartService.addToCart(principal.getName(), isbn, quantity);
        return ApiResponse.success("Đã thêm vào giỏ hàng thành công", null);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateQuantity(
            java.security.Principal principal,
            @RequestParam String isbn,
            @RequestParam Integer quantity) {
        cartService.updateQuantity(principal.getName(), isbn, quantity);
        return ApiResponse.success("Đã cập nhật số lượng", null);
    }

    @DeleteMapping("/remove")
    public ApiResponse<String> removeItem(
            java.security.Principal principal, 
            @RequestParam String isbn) {
        cartService.removeFromCart(principal.getName(), isbn);
        return ApiResponse.success("Đã xóa khỏi giỏ hàng", null);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Dọn sạch giỏ hàng của tôi")
    public ApiResponse<String> clearCart(java.security.Principal principal) {
        cartService.clearCart(principal.getName());
        return ApiResponse.success("Đã dọn sạch giỏ hàng", null);
    }
}
