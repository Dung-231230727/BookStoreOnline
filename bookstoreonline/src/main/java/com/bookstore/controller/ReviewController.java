package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.ReviewDTO;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
@Tag(name = "Review Management", description = "Quản lý đánh giá và nhận xét của khách hàng")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/book/{isbn}")
    @Operation(summary = "Xem đánh giá theo sách", description = "Lấy danh sách các nhận xét và điểm số đánh giá của một cuốn sách cụ thể")
    public ApiResponse<List<ReviewDTO>> getReviewsByBook(@PathVariable String isbn, org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.successPage(reviewService.getReviewsByBook(isbn, pageable));
    }

    @PostMapping("/submit")
    @Operation(summary = "Gửi đánh giá mới", description = "Khách hàng gửi điểm số (1-5 sao) và nhận xét cho cuốn sách đã mua")
    public ApiResponse<String> submitReview(
            java.security.Principal principal,
            @RequestParam String isbn,
            @RequestParam Integer rating,
            @RequestParam String comment) {
        reviewService.submitReview(principal, isbn, rating, comment);
        return ApiResponse.success("Cảm ơn bạn đã gửi đánh giá!", null);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Danh sách toàn bộ đánh giá (Admin)", description = "Lấy toàn bộ danh sách đánh giá trên hệ thống để kiểm duyệt")
    public ApiResponse<List<ReviewDTO>> getAllReviews(org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.successPage(reviewService.getAllReviews(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Xóa đánh giá (Admin)", description = "Xóa một đánh giá không phù hợp")
    public ApiResponse<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success("Đã xóa đánh giá thành công", null);
    }
}
