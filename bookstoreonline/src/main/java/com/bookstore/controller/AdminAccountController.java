package com.bookstore.controller;

import com.bookstore.dto.AdminCreateAccountRequest;
import com.bookstore.dto.AdminUserResponseDTO;
import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.AccountProfileDTO;
import com.bookstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@Tag(name = "Admin Account Management", description = "Quản lý tài khoản người dùng dành cho Quản trị viên")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin("*")
public class AdminAccountController {

    private final AdminService adminService;

    public AdminAccountController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo tài khoản mới cho Nhân viên/Admin", description = "Admin khởi tạo tài khoản kèm Bộ phận. Hồ sơ nhân viên sẽ được tạo ở trạng thái chờ.")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> createAccount(@Valid @RequestBody AdminCreateAccountRequest request) {
        AdminUserResponseDTO response = adminService.createAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo tài khoản thành công", response));
    }


    @GetMapping("/get-users")
    @Operation(summary = "Lấy danh sách toàn bộ người dùng", description = "Admin lấy danh sách tất cả tài khoản kèm thông tin hồ sơ chi tiết")
    public ResponseEntity<ApiResponse<List<AccountProfileDTO>>> getAllUsers() {
        List<AccountProfileDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    @PutMapping("/{username}/status")
    @Operation(summary = "Khóa/Mở tài khoản", description = "Admin cập nhật trạng thái hoạt động của tài khoản. Không được tự khóa chính mình.")
    public ResponseEntity<ApiResponse<String>> updateStatus(
            @PathVariable String username,
            @RequestParam boolean status) {
        adminService.updateUserStatus(username, status);
        String actionMessage = status ? "Mở khóa" : "Khóa";
        return ResponseEntity.ok(ApiResponse.success(actionMessage + " tài khoản thành công", null));
    }

    @PutMapping("/{username}/role")
    @Operation(summary = "Thay đổi quyền của tài khoản", description = "Admin cập nhật Role và tự động đồng bộ hóa Bộ phận tương ứng. Không được tự đổi quyền chính mình.")
    public ResponseEntity<ApiResponse<String>> updateRole(
            @PathVariable String username,
            @RequestParam String role) {
        adminService.updateUserRole(username, role);
        return ResponseEntity.ok(ApiResponse.success("Thay đổi quyền tài khoản thành công", null));
    }
}
