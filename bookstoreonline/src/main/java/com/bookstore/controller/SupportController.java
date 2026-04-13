package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.HoTroDTO;
import com.bookstore.service.HoTroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@CrossOrigin("*")
@Tag(name = "Support Management", description = "Quản lý yêu cầu hỗ trợ và khiếu nại")
public class SupportController {

    private final HoTroService hoTroService;

    public SupportController(HoTroService hoTroService) {
        this.hoTroService = hoTroService;
    }

    @GetMapping
    @Operation(summary = "Tất cả yêu cầu", description = "Admin/Staff xem toàn bộ danh sách các yêu cầu hỗ trợ từ khách hàng")
    public ApiResponse<List<HoTroDTO>> getAllRequests() {
        return ApiResponse.success(hoTroService.layTatCaYeuCau());
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Yêu cầu của tôi", description = "Khách hàng xem lại danh sách các yêu cầu hỗ trợ của chính mình")
    public ApiResponse<List<HoTroDTO>> getRequestsByUser(@PathVariable String username) {
        return ApiResponse.success(hoTroService.layChoKhachHang(username));
    }

    @PostMapping
    @Operation(summary = "Gửi yêu cầu mới", description = "Khách hàng tạo một phiếu (ticket) yêu cầu hỗ trợ mới")
    public ApiResponse<String> createRequest(
            @RequestParam String username,
            @RequestParam String tieuDe,
            @RequestParam String noiDung) {
        hoTroService.guiYeuCau(username, tieuDe, noiDung);
        return ApiResponse.success("Yêu cầu của bạn đã được tiếp nhận", null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật trạng thái", description = "Admin/Staff cập nhật trạng thái xử lý yêu cầu (OPEN, IN_PROGRESS, CLOSED)")
    public ApiResponse<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String trangThai) {
        hoTroService.capNhatTrangThai(id, trangThai);
        return ApiResponse.success("Đã cập nhật trạng thái hồ sơ", null);
    }
}
