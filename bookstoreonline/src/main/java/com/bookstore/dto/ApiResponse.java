package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ApiResponse<T> {

    @Schema(example = "200")
    private int status;

    @Schema(example = "Thành công")
    private String message;

    private T data;
    private Object meta;

    public ApiResponse() {}

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int status, String message, T data, Object meta) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Object getMeta() { return meta; }
    public void setMeta(Object meta) { this.meta = meta; }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Thành công", data);
    }

    public static <T> List<T> success(org.springframework.data.domain.Page<T> page) {
        // Technically this is a helper to return content but we want the whole ApiResponse
        return page.getContent();
    }

    public static <T> ApiResponse<List<T>> successPage(org.springframework.data.domain.Page<T> page) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalElements", page.getTotalElements());
        meta.put("totalPages", page.getTotalPages());
        meta.put("currentPage", page.getNumber());
        meta.put("pageSize", page.getSize());
        meta.put("isFirst", page.isFirst());
        meta.put("isLast", page.isLast());
        
        return new ApiResponse<>(200, "Thành công", page.getContent(), meta);
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
