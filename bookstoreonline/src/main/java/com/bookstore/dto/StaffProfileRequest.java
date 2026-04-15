package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffProfileRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Schema(example = "Nguyễn Văn Nhân Viên")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "Số điện thoại không quá 10 ký tự")
    @Schema(example = "0945667788")
    private String phone;

    public StaffProfileRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
