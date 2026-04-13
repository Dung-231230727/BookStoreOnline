package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.VoucherDTO;
import com.bookstore.service.VoucherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ApiResponse<List<VoucherDTO>> getAllVouchers() {
        return ApiResponse.success(voucherService.layTatCaVoucher());
    }

    @GetMapping("/{code}")
    public ApiResponse<VoucherDTO> getVoucherByCode(@PathVariable String code) {
        return ApiResponse.success(voucherService.layVoucherTheoMa(code));
    }

    @PostMapping
    public ApiResponse<VoucherDTO> createVoucher(@RequestBody VoucherDTO dto) {
        return ApiResponse.created(voucherService.luuVoucher(dto));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<String> deleteVoucher(@PathVariable String code) {
        voucherService.xoaVoucher(code);
        return ApiResponse.success("Đã xóa mã giảm giá thành công", null);
    }
}
