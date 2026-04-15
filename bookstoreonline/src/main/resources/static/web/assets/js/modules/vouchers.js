/**
 * vouchers.js - Marketing & Promotions Logic
 * Aligned with VoucherController.java backend API:
 *   GET    /api/vouchers         ← Danh sách (dùng cho admin list)
 *   GET    /api/vouchers/{code}  ← Validate (dùng cho checkout)
 *   POST   /api/vouchers         ← Tạo mới
 *   DELETE /api/vouchers/{code}  ← Xóa
 *   NOTE: Không có /api/admin/vouchers riêng, tất cả qua /api/vouchers
 */
const vouchers = {

    loadAdminList: async () => {
        try {
            const res = await api.get('/vouchers');  // Correct: /api/vouchers not /api/admin/vouchers
            const data = Array.isArray(res) ? res : (res.data || []);
            vouchers.renderAdminTable(data);
        } catch (e) {
            api.showToast("Không thể tải danh sách khuyến mãi", "error");
        }
    },

    renderAdminTable: (data) => {
        const tbody = $("#vouchers-admin-list");
        if (!tbody.length) return;
        tbody.empty();

        if (!data || data.length === 0) {
            tbody.html('<tr><td colspan="5" class="text-center py-4 text-muted">Chưa có mã giảm giá nào.</td></tr>');
            return;
        }

        data.forEach(v => {
            const isExpired = new Date(v.thoiHan) < new Date();
            tbody.append(`
                <tr>
                    <td class="ps-4">
                        <span class="fw-bold text-accent fs-5">${v.maVoucher}</span>
                    </td>
                    <td>
                        <div class="fw-bold text-dark">${api.formatCurrency(v.giaTriGiam)}</div>
                        <div class="small text-muted">ĐK tối thiểu: ${api.formatCurrency(v.dieuKienToiThieu || 0)}</div>
                    </td>
                    <td>${v.thoiHan ? new Date(v.thoiHan).toLocaleDateString('vi-VN') : '---'}</td>
                    <td>
                        <span class="badge ${isExpired ? 'bg-danger' : 'bg-success'} bg-opacity-10 ${isExpired ? 'text-danger' : 'text-success'} rounded-pill px-3">
                            ${isExpired ? 'Hết hạn' : 'Đang chạy'}
                        </span>
                    </td>
                    <td class="text-end pe-4 d-flex gap-2 justify-content-end">
                        <button onclick="vouchers.openEdit('${v.maVoucher}', ${v.giaTriGiam}, ${v.dieuKienToiThieu || 0}, '${v.thoiHan || ''}')"
                            class="btn btn-sm btn-outline-secondary rounded-pill px-3">Sửa</button>
                        <button onclick="vouchers.delete('${v.maVoucher}')"
                            class="btn btn-sm btn-outline-danger rounded-pill px-3">Xóa</button>
                    </td>
                </tr>
            `);
        });
    },

    // Create new voucher — POST /api/vouchers
    create: async () => {
        const maVoucher        = $("#v-code").val().trim().toUpperCase();
        const giaTriGiam       = parseFloat($("#v-value").val());
        const dieuKienToiThieu = parseFloat($("#v-min").val()) || 0;
        const thoiHan          = $("#v-expire").val();

        if (!maVoucher || !giaTriGiam || !thoiHan) {
            api.showToast("Vui lòng điền đầy đủ thông tin voucher", "warning");
            return;
        }
        try {
            await api.post('/vouchers', { maVoucher, giaTriGiam, dieuKienToiThieu, thoiHan });
            api.showToast("Tạo mã giảm giá thành công!");
            ["#v-code","#v-value","#v-min","#v-expire"].forEach(id => $(id).val(''));
            $('#voucher-create-section').slideUp();
            vouchers.loadAdminList();
        } catch (e) {
            api.showToast("Lỗi tạo voucher: " + e.message, "error");
        }
    },

    openEdit: (code, giaTriGiam, dieuKienToiThieu, thoiHan) => {
        $("#v-edit-code").val(code);
        $("#v-edit-code-display").text(code);
        $("#v-edit-value").val(giaTriGiam);
        $("#v-edit-min").val(dieuKienToiThieu);
        $("#v-edit-expire").val(thoiHan ? thoiHan.split('T')[0] : '');
        $("#voucher-edit-section").slideDown();
    },

    // Update — backend chưa có PUT, dùng delete rồi create lại
    update: async () => {
        const code             = $("#v-edit-code").val();
        const giaTriGiam       = parseFloat($("#v-edit-value").val());
        const dieuKienToiThieu = parseFloat($("#v-edit-min").val()) || 0;
        const thoiHan          = $("#v-edit-expire").val();

        if (!code || !giaTriGiam) {
            api.showToast("Thiếu thông tin cập nhật", "warning");
            return;
        }
        try {
            // Backend chưa có PUT /vouchers/{code}, xóa rồi tạo lại
            await api.delete(`/vouchers/${code}`);
            await api.post('/vouchers', { maVoucher: code, giaTriGiam, dieuKienToiThieu, thoiHan });
            api.showToast("Cập nhật voucher thành công!");
            $('#voucher-edit-section').slideUp();
            vouchers.loadAdminList();
        } catch (e) {
            api.showToast("Lỗi cập nhật voucher: " + e.message, "error");
        }
    },

    delete: async (code) => {
        if (!confirm("Xóa mã giảm giá này?")) return;
        try {
            await api.delete(`/vouchers/${code}`);
            vouchers.loadAdminList();
            api.showToast("Đã xóa mã giảm giá");
        } catch (e) {
            api.showToast("Lỗi khi xóa", "error");
        }
    },

    // Validate voucher at checkout — GET /api/vouchers/{code}
    apply: async (code) => {
        if (!code) { api.showToast("Vui lòng nhập mã giảm giá", "warning"); return; }
        try {
            const res = await api.get(`/vouchers/${code}`);
            const v = res.data || res;
            if (!v || !v.maVoucher) throw new Error("Mã không hợp lệ");
            if (new Date(v.thoiHan) < new Date()) throw new Error("Mã đã hết hạn");

            sessionStorage.setItem('applied_voucher', JSON.stringify(v));
            api.showToast(`Áp dụng thành công: Giảm ${api.formatCurrency(v.giaTriGiam)}`);
            if (typeof orders !== 'undefined' && orders.initCheckout) orders.initCheckout();
        } catch (e) {
            api.showToast(e.message || "Mã giảm giá không chính xác hoặc đã hết hạn", "error");
        }
    }
};
