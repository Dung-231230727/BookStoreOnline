/**
 * payments.js - Payment Management & Transactions
 */

const payments = {
    /**
     * Load admin payments list with filters
     */
    loadAdminPayments: async () => {
        try {
            const search = $("#payment-search").val() || "";
            const status = $("#payment-status-filter").val() || "";
            const method = $("#payment-method-filter").val() || "";

            const res = await api.get(`/payments`, { search, status, method });
            const paymentsList = res.data || res;
            const tbody = $("#payment-list-body");
            if (!tbody.length) return;
            tbody.empty();

            if (!paymentsList || paymentsList.length === 0) {
                tbody.html('<tr><td colspan="7" class="text-center py-5 text-muted">Không tìm thấy giao dịch nào.</td></tr>');
                return;
            }

            paymentsList.forEach(payment => {
                const statusBadge = payments.getStatusBadge(payment.trangThai || payment.statusCode);
                tbody.append(`
                    <tr onclick="layout.render('Payments/Admin', 'Details')">
                        <td class="ps-4 fw-bold">#${payment.maDonHang || payment.orderId}</td>
                        <td>${payment.tenKhachHang || payment.customerName}</td>
                        <td>${payment.phuongThucThanhToan || payment.paymentMethod}</td>
                        <td class="text-end text-accent fw-bold">${formatCurrency(payment.soTien || payment.amount)}</td>
                        <td>${statusBadge}</td>
                        <td>${new Date(payment.thoiGianThanhToan || payment.paymentDate).toLocaleString('vi-VN')}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-circle" onclick="layout.render('Payments/Admin', 'Details')">
                                <i class="icon icon-arrow-right"></i>
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách thanh toán: " + e.message, "error");
        }
    },

    /**
     * Load payment details
     */
    loadPaymentDetails: async (paymentId) => {
        try {
            const res = await api.get(`/payments/${paymentId}`);
            const payment = res.data;

            $("#payment-order-id").text("#" + (payment.maDonHang || payment.orderId));
            $("#payment-transaction-id").text(payment.maGiaoDich || payment.transactionId);
            $("#payment-customer").text(payment.tenKhachHang || payment.customerName);
            $("#payment-customer-email").text(payment.email);
            $("#payment-amount").text(formatCurrency(payment.soTien || payment.amount));
            $("#payment-method").text(payment.phuongThucThanhToan || payment.paymentMethod);
            $("#payment-date").text(new Date(payment.thoiGianThanhToan || payment.paymentDate).toLocaleString('vi-VN'));
            $("#payment-status-badge").text(payment.trangThai || payment.statusCode).className = 
                `badge badge-${payments.getStatusClass(payment.trangThai || payment.statusCode)} rounded-pill px-3 py-2`;
            
            // Set payment details
            $("#payment-bank-code").text((payment.maNganHang || payment.bankCode) || "---");
            $("#payment-ip").text((payment.diaChiIP || payment.ipAddress) || "---");
            $("#payment-description").text((payment.moTa || payment.description) || "---");

            // Load ordered items
            if (payment.chiTiet && payment.chiTiet.length > 0) {
                const itemsList = $("#payment-items-list");
                itemsList.empty();
                payment.chiTiet.forEach(item => {
                    itemsList.append(`
                        <tr>
                            <td>${item.tenSach}</td>
                            <td>${item.soLuong}</td>
                            <td class="text-end">${formatCurrency(item.donGia)}</td>
                        </tr>
                    `);
                });
            }

            // Set totals
            $("#payment-subtotal").text(formatCurrency((payment.tieuPhu || payment.subtotal) || 0));
            $("#payment-shipping").text(formatCurrency((payment.phiVanChuyen || payment.shippingCost) || 0));
            $("#payment-discount").text(formatCurrency((payment.giamGia || payment.discount) || 0));
            $("#payment-total").text(formatCurrency(payment.soTien || payment.amount));
            
            $("#payment-new-status").val(payment.trangThai || payment.statusCode);

            api.showToast("Tải chi tiết thanh toán thành công", "success");
        } catch (e) {
            api.showToast("Lỗi tải chi tiết thanh toán: " + e.message, "error");
        }
    },

    /**
     * Update payment status
     */
    updatePaymentStatus: async (event) => {
        event.preventDefault();
        try {
            const paymentId = new URLSearchParams(window.location.search).get('paymentId');
            const status = $("#payment-new-status").val();
            const note = $("#payment-note").val();

            const res = await api.put(`/payments/${paymentId}`, {
                trangThai: status,
                ghiChu: note
            });

            api.showToast("Cập nhật trạng thái thanh toán thành công!", "success");
            setTimeout(() => {
                payments.loadPaymentDetails(paymentId);
            }, 1000);
        } catch (e) {
            api.showToast("Lỗi cập nhật trạng thái: " + e.message, "error");
        }
    },

    /**
     * Download payment receipt as PDF
     */
    downloadReceipt: async (paymentId) => {
        try {
            window.open(`/payments/${paymentId}/receipt`, '_blank');
            api.showToast("Đang tải hóa đơn...", "info");
        } catch (e) {
            api.showToast("Lỗi tải hóa đơn: " + e.message, "error");
        }
    },

    /**
     * Send payment receipt to customer email
     */
    sendReceipt: async (paymentId) => {
        try {
            const res = await api.post(`/payments/${paymentId}/send-receipt`);
            api.showToast("Đã gửi hóa đơn đến email khách hàng!", "success");
        } catch (e) {
            api.showToast("Lỗi gửi hóa đơn: " + e.message, "error");
        }
    },

    /**
     * Get status badge HTML
     */
    getStatusBadge: (status) => {
        const badges = {
            'PENDING': '<span class="badge bg-warning text-dark">Chờ xác nhận</span>',
            'SUCCESS': '<span class="badge bg-success">Thành công</span>',
            'FAILED': '<span class="badge bg-danger">Thất bại</span>'
        };
        return badges[status] || `<span class="badge bg-secondary">${status}</span>`;
    },

    /**
     * Get status CSS class
     */
    getStatusClass: (status) => {
        const classes = {
            'PENDING': 'warning',
            'SUCCESS': 'success',
            'FAILED': 'danger'
        };
        return classes[status] || 'secondary';
    }
};

/**
 * Format currency to VND
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);
}
