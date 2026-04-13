package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.DanhGiaDTO;
import com.bookstore.service.DanhGiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
@Tag(name = "Review Management", description = "Quản lý đánh giá và nhận xét của khách hàng")
public class ReviewController {

    private final DanhGiaService danhGiaService;

    public ReviewController(DanhGiaService danhGiaService) {
        this.danhGiaService = danhGiaService;
    }

    @GetMapping("/book/{isbn}")
    @Operation(summary = "Xem đánh giá theo sách", description = "Lấy danh sách các nhận xét và điểm số đánh giá của một cuốn sách cụ thể")
    public ApiResponse<List<DanhGiaDTO>> getReviewsByBook(@PathVariable String isbn) {
        return ApiResponse.success(danhGiaService.layDanhGiaTheoSach(isbn));
    }

    @PostMapping("/submit")
    @Operation(summary = "Gửi đánh giá mới", description = "Khách hàng gửi điểm số (1-5 sao) và nhận xét cho cuốn sách đã mua")
    public ApiResponse<String> submitReview(
            @RequestParam String username,
            @RequestParam String isbn,
            @RequestParam Integer diem,
            @RequestParam String nhanXet) {
        danhGiaService.guiDanhGia(username, isbn, diem, nhanXet);
        return ApiResponse.success("Cảm ơn bạn đã gửi đánh giá!", null);
    }
}
