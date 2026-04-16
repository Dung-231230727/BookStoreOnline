package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Upload", description = "Quản lý tải lên tệp tin")
@PreAuthorize("hasRole('ADMIN')")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Tải lên hình ảnh sách", description = "Nhận tệp tin hình ảnh từ thiết bị, lưu vào máy chủ và trả về tên tệp.")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        
        Map<String, String> response = Map.of(
            "fileName", fileName,
            "url", "assets/images/" + fileName
        );
        
        return ResponseEntity.ok(ApiResponse.success("Tải ảnh thành công", response));
    }
}
