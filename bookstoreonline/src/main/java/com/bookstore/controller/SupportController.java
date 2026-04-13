package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.HoTroDTO;
import com.bookstore.service.HoTroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@CrossOrigin("*")
public class SupportController {

    private final HoTroService hoTroService;

    public SupportController(HoTroService hoTroService) {
        this.hoTroService = hoTroService;
    }

    @GetMapping
    public ApiResponse<List<HoTroDTO>> getAllRequests() {
        return ApiResponse.success(hoTroService.layTatCaYeuCau());
    }

    @GetMapping("/user/{username}")
    public ApiResponse<List<HoTroDTO>> getRequestsByUser(@PathVariable String username) {
        return ApiResponse.success(hoTroService.layChoKhachHang(username));
    }

    @PostMapping
    public ApiResponse<String> createRequest(
            @RequestParam String username,
            @RequestParam String tieuDe,
            @RequestParam String noiDung) {
        hoTroService.guiYeuCau(username, tieuDe, noiDung);
        return ApiResponse.success("Yêu cầu của bạn đã được tiếp nhận", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String trangThai) {
        hoTroService.capNhatTrangThai(id, trangThai);
        return ApiResponse.success("Đã cập nhật trạng thái hồ sơ", null);
    }
}
