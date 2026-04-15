package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.SupportTicketDTO;
import com.bookstore.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@CrossOrigin("*")
@Tag(name = "Support Management", description = "Quản lý yêu cầu hỗ trợ và khiếu nại")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping
    @Operation(summary = "Tất cả yêu cầu", description = "Admin/Staff xem toàn bộ danh sách các yêu cầu hỗ trợ từ khách hàng")
    public ApiResponse<List<SupportTicketDTO>> getAllRequests() {
        return ApiResponse.success(supportTicketService.getAllTickets());
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Yêu cầu của tôi", description = "Khách hàng xem lại danh sách các yêu cầu hỗ trợ của chính mình")
    public ApiResponse<List<SupportTicketDTO>> getRequestsByCustomer(@PathVariable String username) {
        return ApiResponse.success(supportTicketService.getTicketsByCustomer(username));
    }

    @PostMapping
    @Operation(summary = "Gửi yêu cầu mới", description = "Khách hàng tạo một phiếu (ticket) yêu cầu hỗ trợ mới")
    public ApiResponse<String> submitTicket(
            @RequestParam String username,
            @RequestParam String subject,
            @RequestParam String content) {
        supportTicketService.submitTicket(username, subject, content);
        return ApiResponse.success("Yêu cầu của bạn đã được tiếp nhận", null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật trạng thái", description = "Admin/Staff cập nhật trạng thái xử lý yêu cầu (OPEN, IN_PROGRESS, CLOSED)")
    public ApiResponse<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        supportTicketService.updateStatus(id, status);
        return ApiResponse.success("Đã cập nhật trạng thái hồ sơ", null);
    }

    @PostMapping("/ai-chat")
    @Operation(summary = "Chatbot AI", description = "Phản hồi tự động cho khách hàng dựa trên từ khóa")
    public ApiResponse<String> aiChat(@RequestParam String message) {
        String msg = message.toLowerCase();
        String response = "Cảm ơn bạn đã nhắn tin! Tôi là AI của Booksaw, tôi có thể giúp bạn tìm sách, kiểm tra đơn hàng hoặc cung cấp mã giảm giá. Bạn cần gì ạ?";

        if (msg.contains("chào") || msg.contains("hello")) {
            response = "Chào bạn! Chúc bạn một ngày tốt lành. Tôi có thể giúp gì cho bạn trong việc chọn sách hôm nay?";
        } else if (msg.contains("giao hàng") || msg.contains("khi nào") || msg.contains("nhận hàng")) {
            response = "Thời gian giao hàng thường từ 2-4 ngày làm việc. Bạn có thể kiểm tra trạng thái trong mục 'Đơn hàng của tôi' nhé!";
        } else if (msg.contains("voucher") || msg.contains("giảm giá") || msg.contains("khuyến mãi")) {
            response = "Hiện tại chúng tôi có mã GIAM20K cho đơn từ 200k. Bạn hãy áp dụng tại bước thanh toán nhé!";
        } else if (msg.contains("sách hay") || msg.contains("tư vấn")) {
            response = "Nếu bạn thích văn học, hãy thử 'Nhà Giả Kim'. Nếu muốn học kỹ năng, 'Đắc Nhân Tâm' là lựa chọn tuyệt vời!";
        } else if (msg.contains("cảm ơn")) {
            response = "Không có gì ạ! Rất vui được hỗ trợ bạn. Chúc bạn đọc sách vui vẻ!";
        }

        return ApiResponse.success(response);
    }
}
