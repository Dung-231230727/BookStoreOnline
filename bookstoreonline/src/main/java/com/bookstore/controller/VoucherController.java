package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.VoucherDTO;
import com.bookstore.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
@Tag(name = "Voucher Management", description = "Quản lý mã giảm giá")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    @Operation(summary = "Danh sách Voucher", description = "Lấy toàn bộ danh sách các mã giảm giá hiện có trên hệ thống")
    public ApiResponse<List<VoucherDTO>> getAllVouchers() {
        return ApiResponse.success(voucherService.getAllVouchers());
    }

    @GetMapping("/{code}")
    @Operation(summary = "Chi tiết Voucher", description = "Kiểm tra thông tin chi tiết của một mã giảm giá qua mã (Code)")
    public ApiResponse<VoucherDTO> getVoucherByCode(@PathVariable String code) {
        return ApiResponse.success(voucherService.getVoucherByCode(code));
    }

    @PostMapping
    @Operation(summary = "Tạo mới Voucher", description = "Tạo một mã giảm giá mới (Dành cho Admin/Staff nạp dữ liệu)")
    public ApiResponse<VoucherDTO> createVoucher(@RequestBody VoucherDTO dto) {
        return ApiResponse.created(voucherService.saveVoucher(dto));
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Xóa Voucher", description = "Xóa mã giảm giá khỏi hệ thống")
    public ApiResponse<String> deleteVoucher(@PathVariable String code) {
        voucherService.deleteVoucher(code);
        return ApiResponse.success("Đã xóa mã giảm giá thành công", null);
    }
}
