package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.DanhGiaDTO;
import com.bookstore.service.DanhGiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    private final DanhGiaService danhGiaService;

    public ReviewController(DanhGiaService danhGiaService) {
        this.danhGiaService = danhGiaService;
    }

    @GetMapping("/book/{isbn}")
    public ApiResponse<List<DanhGiaDTO>> getReviewsByBook(@PathVariable String isbn) {
        return ApiResponse.success(danhGiaService.layDanhGiaTheoSach(isbn));
    }

    @PostMapping("/submit")
    public ApiResponse<String> submitReview(
            @RequestParam String username,
            @RequestParam String isbn,
            @RequestParam Integer diem,
            @RequestParam String nhanXet) {
        danhGiaService.guiDanhGia(username, isbn, diem, nhanXet);
        return ApiResponse.success("Cảm ơn bạn đã gửi đánh giá!", null);
    }
}
