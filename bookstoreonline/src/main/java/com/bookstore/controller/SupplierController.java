package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.SupplierDTO;
import com.bookstore.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/suppliers")
@Tag(name = "Supplier Management", description = "Quản lý nhà cung cấp (Admin)")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách nhà cung cấp")
    public ApiResponse<List<SupplierDTO>> getAllSuppliers() {
        return ApiResponse.success(supplierService.getAllSuppliers());
    }

    @PostMapping
    @Operation(summary = "Thêm nhà cung cấp mới")
    public ApiResponse<SupplierDTO> addSupplier(@RequestBody SupplierDTO request) {
        return ApiResponse.created(supplierService.addSupplier(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nhà cung cấp")
    public ApiResponse<SupplierDTO> updateSupplier(@PathVariable Integer id, @RequestBody SupplierDTO request) {
        return ApiResponse.success("Cập nhật thành công", supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhà cung cấp")
    public ApiResponse<String> deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
        return ApiResponse.success("Xóa thành công nhà cung cấp!", null);
    }
}