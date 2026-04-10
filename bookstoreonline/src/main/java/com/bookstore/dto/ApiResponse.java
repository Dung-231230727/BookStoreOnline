package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cấu trúc Response chung cho toàn bộ API.
 * Mọi Endpoint đều phải trả về kiểu này để Frontend dễ xử lý.
 *
 * @param <T> Kiểu dữ liệu trả về trong trường data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;       // HTTP Status Code (200, 201, 400, 401, 403, 404, 500)
    private String message;   // Thông điệp mô tả kết quả
    private T data;           // Dữ liệu thực tế

    // ─── Factory Methods (tiện lợi cho Controller) ─────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Thành công", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Tạo mới thành công", data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
