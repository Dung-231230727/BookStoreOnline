package com.bookstore.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ChatbotService - AI Chatbot for Booksaw
 * Rule-based NLP engine for book-related customer support
 */
@Service
public class ChatbotService {

    // Quick replies to suggest after each response
    private static final List<String> DEFAULT_QUICK_REPLIES =
        List.of("Tìm sách hay 📚", "Kiểm tra đơn hàng 📦", "Mã giảm giá 🎁", "Thanh toán 💳");

    public Map<String, Object> getResponse(String message) {
        if (message == null || message.isBlank()) {
            return build("Bạn cần hỗ trợ gì ạ? Tôi sẵn sàng giúp bạn!", DEFAULT_QUICK_REPLIES);
        }

        String msg = message.toLowerCase().trim();

        // ── Greeting ─────────────────────────────────────────────────────────
        if (containsAny(msg, "chào", "hello", "hi", "xin chào", "hey", "good morning", "good afternoon")) {
            return build("👋 Chào bạn! Tôi là trợ lý AI của **Booksaw**.\n\nTôi có thể giúp bạn:\n• Tìm kiếm và tư vấn sách\n• Kiểm tra đơn hàng\n• Hỗ trợ thanh toán & hoàn trả\n• Áp dụng mã giảm giá\n\nBạn cần hỗ trợ gì hôm nay?",
                List.of("Tư vấn sách 📚", "Đơn hàng của tôi 📦", "Mã giảm giá 🎁", "Chính sách đổi trả 🔄"));
        }

        // ── Thanks / Goodbye ──────────────────────────────────────────────────
        if (containsAny(msg, "cảm ơn", "camon", "thanks", "thank you", "tạm biệt", "bye", "goodbye")) {
            return build("😊 Không có gì ạ! Rất vui được hỗ trợ bạn.\nNếu cần thêm gì, bạn cứ nhắn nhé. Chúc bạn đọc sách vui vẻ! 📖",
                List.of("Tìm sách tiếp 📚", "Quay lại sau 👋"));
        }

        // ── Book recommendations ──────────────────────────────────────────────
        if (containsAny(msg, "sách hay", "gợi ý sách", "tư vấn sách", "nên đọc", "recommend", "sách tốt", "sách nào", "sách gì")) {
            if (containsAny(msg, "kinh doanh", "kinh tế", "startup", "khởi nghiệp", "làm giàu", "đầu tư")) {
                return build("💼 **Sách Kinh doanh hay nhất**:\n\n📘 *Từ Tốt Đến Vĩ Đại* — Jim Collins\n📘 *Khởi Nghiệp Tinh Gọn* — Eric Ries\n📘 *Dạy Con Làm Giàu* — Robert Kiyosaki\n📘 *Tư Duy Nhanh Và Chậm* — Daniel Kahneman\n\nBạn muốn tôi tìm sách nào trong kho không?",
                    List.of("Tìm sách kinh doanh 🔍", "Xem giá sách 💰", "Thêm vào giỏ 🛒"));
            }
            if (containsAny(msg, "kỹ năng", "phát triển bản thân", "tự học", "thành công", "tư duy", "leadership", "lãnh đạo")) {
                return build("🧠 **Sách Phát triển bản thân hay nhất**:\n\n📗 *Đắc Nhân Tâm* — Dale Carnegie\n📗 *7 Thói Quen Hiệu Quả* — Stephen Covey\n📗 *Nghĩ Giàu Làm Giàu* — Napoleon Hill\n📗 *Atomic Habits* — James Clear\n\nTất cả đều có tại Booksaw!",
                    List.of("Tìm sách kỹ năng 🔍", "Sách dưới 200k 💰", "Thêm vào giỏ 🛒"));
            }
            if (containsAny(msg, "văn học", "tiểu thuyết", "truyện", "fiction", "novel")) {
                return build("📖 **Văn học hay được yêu thích**:\n\n📕 *Nhà Giả Kim* — Paulo Coelho\n📕 *Số Đỏ* — Vũ Trọng Phụng\n📕 *Mắt Biếc* — Nguyễn Nhật Ánh\n📕 *Tôi Thấy Hoa Vàng Trên Cỏ Xanh*\n\nBạn có muốn xem thêm không?",
                    List.of("Xem văn học nước ngoài 📚", "Văn học Việt Nam 🇻🇳", "Đặt mua ngay 🛒"));
            }
            if (containsAny(msg, "lập trình", "công nghệ", "code", "python", "java", "it")) {
                return build("💻 **Sách Lập trình & Công nghệ**:\n\n📙 *Clean Code* — Robert C. Martin\n📙 *Python Crash Course*\n📙 *The Pragmatic Programmer*\n📙 *Designing Data-Intensive Applications*\n\nBooksaw có đầy đủ sách IT tiếng Anh và dịch!",
                    List.of("Tìm sách lập trình 🔍", "Sách IT dưới 300k 💰", "Xem tất cả sách 📚"));
            }
            if (containsAny(msg, "thiếu nhi", "trẻ em", "con", "bé", "kids", "children")) {
                return build("👶 **Sách cho thiếu nhi**:\n\n📙 *Doraemon* (trọn bộ)\n📙 *Truyện Cổ Tích Việt Nam*\n📙 *Rèn Luyện Tư Duy — Toán IQ*\n📙 *Khoa Học Vui* cho bé\n\nPhù hợp theo độ tuổi từ 3-15 tuổi!",
                    List.of("Sách 3-6 tuổi 👶", "Sách 6-12 tuổi 🧒", "Sách thiếu niên 👦"));
            }
            // Generic recommendation
            return build("📚 **Top sách bán chạy tại Booksaw**:\n\n🥇 *Đắc Nhân Tâm* — Kỹ năng giao tiếp\n🥈 *Nhà Giả Kim* — Văn học triết lý\n🥉 *Atomic Habits* — Xây dựng thói quen\n4️⃣ *Khởi Nghiệp Tinh Gọn* — Kinh doanh\n5️⃣ *Tư Duy Nhanh Và Chậm* — Tâm lý học\n\nBạn muốn tìm theo thể loại nào?",
                List.of("Kinh doanh 💼", "Tâm lý - Kỹ năng 🧠", "Văn học 📖", "Thiếu nhi 👶"));
        }

        // ── Order / Delivery ──────────────────────────────────────────────────
        if (containsAny(msg, "đơn hàng", "order", "mua rồi", "đã đặt", "trạng thái đơn")) {
            return build("📦 **Kiểm tra đơn hàng**:\n\n1. Đăng nhập tài khoản\n2. Vào **Tài khoản → Lịch sử đơn hàng**\n3. Hoặc nhấn nút bên dưới để tra cứu ngay\n\nNếu bạn chưa thấy đơn, hãy đợi 5-10 phút sau khi đặt nhé!",
                List.of("Xem đơn hàng 📋", "Tra cứu vận đơn 🚚", "Liên hệ hỗ trợ 📞"));
        }

        if (containsAny(msg, "giao hàng", "ship", "vận chuyển", "khi nào nhận", "bao lâu", "mấy ngày")) {
            return build("🚚 **Thời gian giao hàng**:\n\n• **Nội thành HCM/HN**: 1-2 ngày\n• **Tỉnh thành khác**: 2-4 ngày\n• **Vùng sâu vùng xa**: 4-7 ngày\n\n📌 Miễn phí vận chuyển cho đơn từ **300.000đ**!\n\nBạn có thể tra cứu vận đơn tại mục Dịch vụ → Tra cứu vận đơn.",
                List.of("Tra cứu vận đơn 🔍", "Đơn hàng của tôi 📦", "Hỏi thêm 💬"));
        }

        // ── Discount / Voucher ────────────────────────────────────────────────
        if (containsAny(msg, "voucher", "mã giảm giá", "coupon", "khuyến mãi", "giảm giá", "sale", "ưu đãi", "mã code")) {
            return build("🎁 **Mã giảm giá hiện có**:\n\n🏷️ **BOOKSAW10** — Giảm 10% đơn từ 200k\n🏷️ **NEWMEMBER** — Giảm 20k cho thành viên mới\n🏷️ **FREESHIP** — Miễn phí ship đơn từ 150k\n\n💡 Áp dụng tại bước **Thanh toán** nhé!\n\n*Lưu ý: Mỗi mã chỉ dùng 1 lần/tài khoản*",
                List.of("Mua hàng ngay 🛒", "Xem sách hay 📚", "Điều kiện áp dụng ❓"));
        }

        // ── Payment ───────────────────────────────────────────────────────────
        if (containsAny(msg, "thanh toán", "payment", "trả tiền", "chuyển khoản", "momo", "vnpay", "atm", "thẻ", "tiền mặt", "cod")) {
            return build("💳 **Phương thức thanh toán tại Booksaw**:\n\n✅ **VNPay QR** — Quét mã nhanh\n✅ **Chuyển khoản ngân hàng**\n✅ **COD** — Thanh toán khi nhận\n✅ **Thẻ ATM / Visa / Mastercard**\n\n🔒 Mọi giao dịch đều được mã hóa bảo mật SSL.",
                List.of("Thanh toán VNPay 📱", "COD là gì? ❓", "Đặt hàng luôn 🛒"));
        }

        // ── Return / Refund ───────────────────────────────────────────────────
        if (containsAny(msg, "đổi trả", "hoàn tiền", "refund", "trả hàng", "đổi sách", "hỏng", "lỗi", "thiếu trang")) {
            return build("🔄 **Chính sách đổi trả**:\n\n• Đổi trả trong **7 ngày** từ khi nhận hàng\n• Điều kiện: sách còn nguyên vẹn, chưa bóc niêm phong\n• Sách lỗi nhà sản xuất: đổi miễn phí\n• Sách hỏng khi vận chuyển: hoàn tiền 100%\n\n📞 Liên hệ CSKH: **1800-xxxx** (miễn phí)",
                List.of("Yêu cầu đổi trả 📝", "Liên hệ CSKH 📞", "Xem điều khoản 📋"));
        }

        // ── Account / Login ───────────────────────────────────────────────────
        if (containsAny(msg, "tài khoản", "đăng nhập", "đăng ký", "mật khẩu", "quên mật khẩu", "login", "register", "account")) {
            return build("👤 **Hỗ trợ tài khoản**:\n\n• **Quên mật khẩu**: Nhấn \"Quên mật khẩu\" tại trang đăng nhập\n• **Chưa có tài khoản**: Đăng ký miễn phí, nhận ngay mã **NEWMEMBER**\n• **Lỗi đăng nhập**: Thử xóa cache trình duyệt\n\nBạn đang gặp vấn đề cụ thể gì?",
                List.of("Đăng nhập 🔐", "Đăng ký mới ✨", "Quên mật khẩu 🔑"));
        }

        // ── Price / Cost ──────────────────────────────────────────────────────
        if (containsAny(msg, "giá", "bao nhiêu", "price", "cost", "phí", "tiền", "rẻ", "đắt")) {
            return build("💰 **Giá sách tại Booksaw**:\n\n• Sách thiếu nhi: từ **30.000đ**\n• Văn học trong nước: từ **50.000đ**\n• Sách dịch: từ **80.000đ**\n• Sách IT / chuyên ngành: từ **150.000đ**\n\n🎁 Miễn phí ship đơn từ **300.000đ**!\n\nBạn muốn tìm sách trong tầm giá nào?",
                List.of("Sách dưới 100k 💸", "Sách 100-300k 💰", "Xem tất cả 📚"));
        }

        // ── Search ────────────────────────────────────────────────────────────
        if (containsAny(msg, "tìm sách", "tìm kiếm", "search", "có sách", "còn sách")) {
            return build("🔍 **Tìm sách tại Booksaw**:\n\nBạn có thể:\n1. Dùng **AI Search** ở thanh tìm kiếm — nhập tự nhiên như \"sách kỹ năng dưới 200k\"\n2. Duyệt theo **Danh mục** từ menu\n3. Nhắn tên sách bạn muốn tìm cho tôi!\n\nBạn đang tìm sách về chủ đề gì?",
                List.of("Nhập tên sách 📝", "Duyệt danh mục 📂", "AI Search 🤖"));
        }

        // ── Contact / Human support ───────────────────────────────────────────
        if (containsAny(msg, "liên hệ", "gặp người thật", "nhân viên", "hotline", "phone", "email", "contact", "cskh", "tư vấn viên")) {
            return build("📞 **Liên hệ Booksaw**:\n\n• **Hotline**: 1800-xxxx (7h-22h, miễn phí)\n• **Email**: support@booksaw.vn\n• **Facebook**: fb.com/booksaw\n• **Zalo OA**: Booksaw Official\n\nHoặc gửi ticket hỗ trợ, chúng tôi phản hồi trong **2 giờ** làm việc!",
                List.of("Gửi ticket hỗ trợ 📝", "Chat Facebook 💬", "Gọi ngay 📞"));
        }

        // ── Default fallback ──────────────────────────────────────────────────
        return build("🤔 Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn.\n\nTôi có thể hỗ trợ bạn về:\n• 📚 **Tư vấn & tìm sách**\n• 📦 **Đơn hàng & vận chuyển**\n• 💳 **Thanh toán**\n• 🎁 **Mã giảm giá**\n• 🔄 **Đổi trả & hoàn tiền**\n\nBạn muốn hỏi về chủ đề nào?",
            DEFAULT_QUICK_REPLIES);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private Map<String, Object> build(String message, List<String> quickReplies) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", message);
        result.put("quickReplies", quickReplies);
        return result;
    }
}
