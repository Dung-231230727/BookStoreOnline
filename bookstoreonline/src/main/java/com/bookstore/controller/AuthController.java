package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.ChangePasswordRequest;
import com.bookstore.dto.ForgotPasswordRequest;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.LoginResponse;
import com.bookstore.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Xác thực đăng nhập và phân quyền")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Xác thực đăng nhập", description = "Kiểm tra username/password và trả về thông tin tài khoản kèm JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản khách hàng (CUSTOMER) mới")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", response));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Kiểm tra mật khẩu cũ và cập nhật mật khẩu mới cho tài khoản hiện tại")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu", description = "Gửi yêu cầu đặt lại mật khẩu qua email (Giả lập)")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Yêu cầu đã được ghi nhận. Vui lòng kiểm tra email của bạn.", null));
    }
}
