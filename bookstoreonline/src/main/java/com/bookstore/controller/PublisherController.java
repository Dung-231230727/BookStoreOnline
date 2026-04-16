package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.PublisherDTO;
import com.bookstore.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Lấy danh sách nhà xuất bản", description = "Lấy danh sách tất cả các nhà xuất bản hiện có.")
    public ResponseEntity<ApiResponse<List<PublisherDTO>>> getAllPublishers(org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<PublisherDTO> publishers = publisherService.getAllPublishers(pageable);
        return ResponseEntity.ok(ApiResponse.successPage(publishers));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm NXB mới (ADMIN)", description = "Admin thêm nhà xuất bản mới.")
    public ResponseEntity<ApiResponse<PublisherDTO>> createPublisher(@RequestBody PublisherDTO dto) {
        return ResponseEntity.status(201).body(ApiResponse.created(publisherService.savePublisher(dto)));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật NXB (ADMIN)", description = "Admin cập nhật thông tin nhà xuất bản dựa trên mã ID.")
    public ResponseEntity<ApiResponse<PublisherDTO>> updatePublisher(@PathVariable Integer id, @RequestBody PublisherDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật NXB thành công", publisherService.updatePublisher(id, dto)));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa NXB (ADMIN)", description = "Admin xóa nhà xuất bản khỏi hệ thống nếu không có sách liên kết.")
    public ResponseEntity<ApiResponse<Void>> deletePublisher(@PathVariable Integer id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa NXB thành công", null));
    }
}
