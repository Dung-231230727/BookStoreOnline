package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.PublisherDTO;
import com.bookstore.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@Tag(name = "Publishers", description = "Quản lý dữ liệu Nhà xuất bản")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách NXB", description = "Lấy danh sách tất cả nhà xuất bản hiện có trong hệ thống phục vụ tạo nhanh sách hoặc bộ lọc tìm kiếm.")
    public ResponseEntity<ApiResponse<List<PublisherDTO>>> getAllPublishers() {
        List<PublisherDTO> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhà xuất bản thành công", publishers));
    }
}
