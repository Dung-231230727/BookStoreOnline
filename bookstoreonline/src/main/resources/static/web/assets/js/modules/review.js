/**
 * review.js - Product Reviews & Feedback Logic
 */
const review = {
    // Client: Load reviews for a specific book
    loadForBook: async (isbn) => {
        try {
            const res = await api.get(`/reviews/book/${isbn}`);
            if (res.status === 200) {
                review.renderSection(res.data);
            }
        } catch (e) { console.error("Reviews load failed", e); }
    },

    renderSection: (data) => {
        const container = $("#reviews-container");
        if (!container.length) return;
        
        container.empty();
        if (!data || data.length === 0) {
            container.html('<p class="text-muted italic">Chưa có đánh giá nào cho sản phẩm này.</p>');
            return;
        }

        data.forEach(r => {
            const stars = '★'.repeat(r.diemDg) + '☆'.repeat(5 - r.diemDg);
            container.append(`
                <div class="review-item mb-4 pb-4 border-bottom">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="fw-bold text-dark">${r.tenKhachHang || 'Khách hàng'}</span>
                        <span class="text-warning">${stars}</span>
                    </div>
                    <p class="text-muted mb-1">${r.nhanXet}</p>
                    <small class="text-muted-50">${Array.isArray(r.ngayDg) ? new Date(r.ngayDg[0], r.ngayDg[1]-1, r.ngayDg[2]).toLocaleDateString('vi-VN') : new Date(r.ngayDg).toLocaleDateString('vi-VN')}</small>
                </div>
            `);
        });
    },

    // Client: Submit new review
    submit: async (isbn) => {
        const user = api.getUser();
        if (!user) {
            api.showToast("Cần đăng nhập để đánh giá", "warning");
            return;
        }

        const diem = $("#review-rating").val();
        const noidung = $("#review-content").val();

        if (!noidung) {
            api.showToast("Vui lòng nhập nội dung đánh giá", "warning");
            return;
        }

        try {
            const res = await api.post(`/reviews/submit?username=${user.username}&isbn=${isbn}&diem=${diem}&nhanXet=${encodeURIComponent(noidung)}`);

            if (res.status === 200) {
                api.showToast("Cảm ơn đánh giá của bạn!");
                $("#review-form").trigger("reset");
                review.loadForBook(isbn);
            }
        } catch (e) {
            api.showToast("Không thể gửi đánh giá lúc này", "error");
        }
    },

    // Admin: Load all reviews for moderation
    loadAdminList: async () => {
        try {
            // Note: The /api/admin/reviews endpoint is listed in api.txt but not yet implemented in ReviewController.
            // Temporarily stubbing data to prevent page crashes.
            setTimeout(() => {
                 const tbody = $("#reviews-admin-list");
                 if (tbody.length) {
                     tbody.html('<tr><td colspan="5" class="text-center py-5 text-muted">Tính năng kiểm duyệt đánh giá đang được Backend cập nhật (API chưa sẵn sàng)</td></tr>');
                 }
            }, 500);
        } catch (e) { api.showToast("Lỗi tải danh sách đánh giá", "error"); }
    },

    renderAdminTable: (data) => {
        const tbody = $("#reviews-admin-list");
        if (!tbody.length) return;
        tbody.empty();

        data.forEach(r => {
            tbody.append(`
                <tr>
                    <td class="ps-4">
                        <div class="fw-bold">${r.tenKhachHang || 'Khách hàng'}</div>
                        <div class="small text-muted">ISBN: ${r.isbn || '---'}</div>
                    </td>
                    <td><span class="text-warning">${'★'.repeat(r.diemDg || 0)}</span></td>
                    <td style="max-width: 300px;" class="text-truncate">${r.nhanXet}</td>
                    <td>${Array.isArray(r.ngayDg) ? new Date(r.ngayDg[0], r.ngayDg[1]-1, r.ngayDg[2]).toLocaleDateString('vi-VN') : new Date(r.ngayDg).toLocaleDateString('vi-VN')}</td>
                    <td class="text-end pe-4">
                        <button onclick="review.delete(${r.maDg})" class="btn btn-sm btn-outline-danger border-0">Xóa</button>
                    </td>
                </tr>
            `);
        });
    },

    delete: async (id) => {
        if (!confirm("Xóa đánh giá này?")) return;
        try {
            await api.delete(`/admin/reviews/${id}`);
            review.loadAdminList();
            api.showToast("Đã xóa đánh giá");
        } catch (e) { api.showToast("Lỗi khi xóa", "error"); }
    }
};
