package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.GioHangDTO;
import com.bookstore.service.GioHangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
@Tag(name = "Cart Management", description = "Quản lý giỏ hàng của khách hàng")
public class CartController {

    private final GioHangService gioHangService;

    public CartController(GioHangService gioHangService) {
        this.gioHangService = gioHangService;
    }

    @GetMapping("/{username}")
    @Operation(summary = "Lấy giỏ hàng", description = "Xem danh sách các sản phẩm đang có trong giỏ hàng của người dùng")
    public ApiResponse<List<GioHangDTO>> getCart(@PathVariable String username) {
        return ApiResponse.success(gioHangService.layGioHang(username));
    }

    @PostMapping("/add")
    @Operation(summary = "Thêm vào giỏ", description = "Thêm một cuốn sách vào giỏ hàng hoặc tăng số lượng nếu đã tồn tại")
    public ApiResponse<String> addToCart(
            @RequestParam String username,
            @RequestParam String isbn,
            @RequestParam Integer soLuong) {
        gioHangService.themVaoGioHang(username, isbn, soLuong);
        return ApiResponse.success("Đã thêm vào giỏ hàng thành công", null);
    }

    @PutMapping("/update")
    @Operation(summary = "Cập nhật số lượng", description = "Thay đổi số lượng của một mục trong giỏ hàng")
    public ApiResponse<String> updateQuantity(
            @RequestParam Long id,
            @RequestParam Integer soLuong) {
        gioHangService.capNhatSoLuong(id, soLuong);
        return ApiResponse.success("Đã cập nhật số lượng", null);
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Xóa một mục", description = "Xóa bỏ hoàn toàn một cuốn sách khỏi giỏ hàng")
    public ApiResponse<String> removeItem(@PathVariable Long id) {
        gioHangService.xoaKhoiGioHang(id);
        return ApiResponse.success("Đã xóa khỏi giỏ hàng", null);
    }

    @DeleteMapping("/clear/{username}")
    @Operation(summary = "Dọn sạch giỏ hàng", description = "Xóa toàn bộ các sản phẩm trong giỏ hàng của người dùng")
    public ApiResponse<String> clearCart(@PathVariable String username) {
        gioHangService.lamTrongGioHang(username);
        return ApiResponse.success("Đã dọn sạch giỏ hàng", null);
    }
}
