package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.ChangePasswordRequest;
import com.bookstore.dto.CustomerProfileRequest;
import com.bookstore.dto.StaffProfileRequest;
import com.bookstore.dto.AccountProfileDTO;
import com.bookstore.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
@CrossOrigin("*")
@Tag(name = "User Profile Management", description = "Quản lý thông tin cá nhân tài khoản (Customer/Staff)")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    @Operation(summary = "Get current profile", description = "Lấy thông tin profile của tài khoản đang đăng nhập")
    public ResponseEntity<ApiResponse<AccountProfileDTO>> getProfile() {
        AccountProfileDTO profile = accountService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Success", profile));
    }

    @PutMapping("/update-customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update Customer profile")
    public ResponseEntity<ApiResponse<AccountProfileDTO>> updateCustomerProfile(@Valid @RequestBody CustomerProfileRequest request) {
        AccountProfileDTO updated = accountService.updateCustomerProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Customer profile updated", updated));
    }

    @PutMapping("/update-staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN') or hasRole('STOREKEEPER')")
    @Operation(summary = "Update Staff profile")
    public ResponseEntity<ApiResponse<AccountProfileDTO>> updateStaffProfile(@Valid @RequestBody StaffProfileRequest request) {
        AccountProfileDTO updated = accountService.updateStaffProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Staff profile updated", updated));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Initialize profile")
    public ResponseEntity<ApiResponse<AccountProfileDTO>> createCustomerProfile(@Valid @RequestBody CustomerProfileRequest request) {
        AccountProfileDTO profile = accountService.createCustomerProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Initial profile created", profile));
    }
}
