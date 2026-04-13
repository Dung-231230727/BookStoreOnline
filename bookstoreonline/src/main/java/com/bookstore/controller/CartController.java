package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.GioHangDTO;
import com.bookstore.service.GioHangService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final GioHangService gioHangService;

    public CartController(GioHangService gioHangService) {
        this.gioHangService = gioHangService;
    }

    @GetMapping("/{username}")
    public ApiResponse<List<GioHangDTO>> getCart(@PathVariable String username) {
        return ApiResponse.success(gioHangService.layGioHang(username));
    }

    @PostMapping("/add")
    public ApiResponse<String> addToCart(
            @RequestParam String username,
            @RequestParam String isbn,
            @RequestParam Integer soLuong) {
        gioHangService.themVaoGioHang(username, isbn, soLuong);
        return ApiResponse.success("Đã thêm vào giỏ hàng thành công", null);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateQuantity(
            @RequestParam Long id,
            @RequestParam Integer soLuong) {
        gioHangService.capNhatSoLuong(id, soLuong);
        return ApiResponse.success("Đã cập nhật số lượng", null);
    }

    @DeleteMapping("/remove/{id}")
    public ApiResponse<String> removeItem(@PathVariable Long id) {
        gioHangService.xoaKhoiGioHang(id);
        return ApiResponse.success("Đã xóa khỏi giỏ hàng", null);
    }

    @DeleteMapping("/clear/{username}")
    public ApiResponse<String> clearCart(@PathVariable String username) {
        gioHangService.lamTrongGioHang(username);
        return ApiResponse.success("Đã dọn sạch giỏ hàng", null);
    }
}
