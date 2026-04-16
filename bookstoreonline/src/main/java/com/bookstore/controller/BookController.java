package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import com.bookstore.service.AiSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.bookstore.dto.BookCreateRequest;
import com.bookstore.dto.BookUpdateRequest;
import com.bookstore.dto.AiSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Books", description = "Quản lý thông tin sách")
public class BookController {

    private final BookService bookService;
    private final AiSearchService aiSearchService;

    public BookController(BookService bookService, AiSearchService aiSearchService) {
        this.bookService = bookService;
        this.aiSearchService = aiSearchService;
    }

    @GetMapping("/books")
    @Operation(summary = "Lấy danh sách và tìm kiếm sách", description = "Tìm kiếm và lọc sách theo từ khóa, danh mục (ID), khoảng giá và trạng thái.")
    public ApiResponse<java.util.List<BookDTO>> getAllBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @org.springframework.data.web.PageableDefault(size = 50) org.springframework.data.domain.Pageable pageable) {
        
        // Default to ACTIVE for non-admin search if status is null or hidden
        com.bookstore.enums.BookStatus bookStatus = null;
        if (status != null && !status.isEmpty()) {
            bookStatus = com.bookstore.enums.BookStatus.valueOf(status.toUpperCase());
        }
        
        return ApiResponse.successPage(bookService.searchAndFilterBooks(query, categoryId, bookStatus, minPrice, maxPrice, pageable));
    }

    @GetMapping("/books/search")
    @Operation(summary = "Tìm kiếm sách nâng cao (Alias)", description = "Sử dụng cho các chức năng lọc nâng cao từ Frontend.")
    public ApiResponse<java.util.List<BookDTO>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @org.springframework.data.web.PageableDefault(size = 50) org.springframework.data.domain.Pageable pageable) {
        return getAllBooks(query, categoryId, status, minPrice, maxPrice, pageable);
    }

    @PostMapping("/books/ai-search")
    @Operation(summary = "Tìm kiếm sách bằng AI (NLP)", description = "Phân tích câu nói tự nhiên (Ví dụ: 'find economics books under 50k') để tự bóc tách mức giá và danh mục.")
    public ResponseEntity<ApiResponse<List<BookDTO>>> aiSearchBooks(
            @Valid @RequestBody AiSearchRequest request) {
        List<BookDTO> books = aiSearchService.searchByNaturalLanguage(request.getQuery());
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm AI thành công", books));
    }

    @GetMapping("/books/{isbn}")
    @Operation(summary = "Xem chi tiết sách", description = "Lấy thông tin chi tiết của một cuốn sách bao gồm các thông tin tác giả, NXB cấu thành dựa trên mã ISBN.")
    public ResponseEntity<ApiResponse<BookDTO>> getBookDetail(
            @PathVariable String isbn) {
        BookDTO book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chi tiết sách thành công", book));
    }

    @PostMapping("/admin/books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm sách mới (ADMIN)", description = "Admin thêm sách mới kèm theo danh mục, nhà xuất bản và danh sách tác giả.")
    public ResponseEntity<ApiResponse<BookDTO>> createBook(
            @Valid @RequestBody BookCreateRequest request) {
        BookDTO created = bookService.createBook(request);
        return ResponseEntity.status(201).body(ApiResponse.created(created));
    }

    @PutMapping("/admin/books/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sửa thông tin sách (ADMIN)", description = "Cập nhật thông tin chi tiết của sách (tên, giá, mô tả, NXB, danh mục, tác giả) dựa trên mã ISBN.")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(
            @PathVariable String isbn,
            @Valid @RequestBody BookUpdateRequest request) {
        BookDTO updated = bookService.updateBook(isbn, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin sách thành công", updated));
    }

    @DeleteMapping("/admin/books/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa mềm sách (ADMIN)", description = "Đánh dấu sách là đã xóa (xóa mềm) trên hệ thống mà không làm mất dữ liệu lịch sử.")
    public ResponseEntity<ApiResponse<String>> deleteBook(
            @PathVariable String isbn) {
        bookService.softDeleteBook(isbn);
        return ResponseEntity.ok(ApiResponse.success("Xoa sách thành công", null));
    }

    @PatchMapping("/admin/books/{isbn}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái nhanh (ADMIN)", description = "Chỉnh sửa nhanh trạng thái sách (ACTIVE, INACTIVE, etc.) từ danh sách admin.")
    public ResponseEntity<ApiResponse<BookDTO>> updateBookStatus(
            @PathVariable String isbn,
            @RequestParam String status) {
        BookDTO updated = bookService.updateStatus(isbn, com.bookstore.enums.BookStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", updated));
    }
}
