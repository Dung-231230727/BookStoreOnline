/**
 * orders.js - Payment & Order Management
 */

// Helper: Spring LocalDateTime may serialize as array [yr,mo,day,hr,min,sec] or ISO string
function formatOrderDate(ngayTao, withTime = false) {
    if (!ngayTao) return '---';
    let d;
    if (Array.isArray(ngayTao)) {
        // [2024, 4, 14, 10, 30, 0] → month is 1-indexed in Java, JS Date is 0-indexed
        d = new Date(ngayTao[0], ngayTao[1] - 1, ngayTao[2], ngayTao[3] || 0, ngayTao[4] || 0);
    } else {
        d = new Date(ngayTao);
    }
    if (isNaN(d)) return String(ngayTao);
    return withTime
        ? d.toLocaleString('vi-VN')
        : d.toLocaleDateString('vi-VN');
}

const orders = {
    // 1. Checkout Process
    initCheckout: async () => {
        const user = api.getUser();
        if (user) {
            $("#customer-name").val(user.hoTen || user.username);
            $("#customer-email").val(user.email);
            $("#customer-phone").val(user.soDienThoai);
            $("#customer-address").val(user.diaChi);
        }
        orders.renderSummary();
    },

    renderSummary: async () => {
        const user = api.getUser();
        let cartData = [];
        if (user) {
             const res = await api.get(`/cart/${user.username}`);
             cartData = res.data || [];
        }
        
        const container = $("#checkout-summary");
        if (!container.length) return;
        container.empty();
        
        let total = 0;
        cartData.forEach(item => {
            total += item.giaNiemYet * item.soLuong;
            container.append(`
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">${item.tenSach} x${item.soLuong}</span>
                    <span class="fw-bold">${api.formatCurrency(item.giaNiemYet * item.soLuong)}</span>
                </div>
            `);
        });

        const shipping = total > 500000 ? 0 : 30000;
        $("#summary-subtotal").text(api.formatCurrency(total));
        $("#summary-shipping").text(shipping === 0 ? "Miễn phí" : api.formatCurrency(shipping));
        $("#summary-total").text(api.formatCurrency(total + shipping));
    },

    applyVoucher: async () => {
        const voucherCode = $("#voucher-input").val().trim();
        if (!voucherCode) {
            $("#voucher-message").text("Vui lòng nhập mã giảm giá").addClass("text-danger").show();
            return;
        }

        try {
            const res = await api.post(`/vouchers/apply/${voucherCode}`);
            const voucher = res.data;
            sessionStorage.setItem('applied_voucher', JSON.stringify(voucher));
            
            const msgDiv = $("#voucher-message");
            msgDiv.html(`<i class="icon icon-check text-success me-2"></i><span class="text-success">Áp dụng thành công! Giảm ${api.formatCurrency(voucher.giaTriGiam)}</span>`)
                .removeClass("text-danger").addClass("text-success").show();
            
            orders.renderSummary(); // Re-render với discount
        } catch (e) {
            const msgDiv = $("#voucher-message");
            msgDiv.text("Mã giảm giá không hợp lệ hoặc đã hết hạn").addClass("text-danger").show();
        }
    },

    processCheckout: async () => {
        const form = $("#checkout-form");
        if (!form[0].checkValidity()) {
            form[0].reportValidity();
            return;
        }

        const user = api.getUser();
        if (!user) return;
        const res = await api.get(`/cart/${user.username}`);
        const cartData = res.data || [];
        
        if (cartData.length === 0) {
            api.showToast("Giỏ hàng của bạn đang trống!", "error");
            return;
        }

        const paymentMethod = $("input[name='paymentMethod']:checked").val();
        const orderData = {
            username: user.username,
            maVoucher: sessionStorage.getItem('applied_voucher') ? JSON.parse(sessionStorage.getItem('applied_voucher')).maVoucher : "",
            diaChiGiaoHang: $("#customer-address").val(),
            phuongThucThanhToan: paymentMethod
        };

        api.showToast("Đang khởi tạo đơn hàng...", "info");
        try {
            // 1. Tạo đơn hàng cơ bản
            const res = await api.post('/orders/checkout', orderData);
            const orderId = res.data.maDonHang;

            if (paymentMethod === 'VNPAY') {
                // 2. Nếu là VNPay, gọi tiếp API lấy URL thanh toán
                const vnpayRes = await api.get(`/payments/vnpay/create?orderId=${orderId}`);
                window.location.href = vnpayRes.data; // Chuyển hướng sang cổng VNPay
            } else if (paymentMethod === 'MOMO') {
                // 2b. Nếu là MOMO, gọi API lấy URL thanh toán MOMO (tương tự VNPay)
                const momoRes = await api.get(`/payments/momo/create?orderId=${orderId}`);
                window.location.href = momoRes.data; // Chuyển hướng sang cổng MOMO
            } else {
                // 3. Thanh toán COD
                api.showToast("Đơn hàng đã được đặt thành công!");
                localStorage.removeItem('cart');
                layout.render('Orders', 'PaymentResult', orderId);
            }
        } catch (error) {
            api.showToast("Lỗi khi tạo đơn hàng: " + error.message, "error");
        }
    },

    trackOrder: async (orderId) => {
        try {
            const res = await api.get(`/shipping/tracking/${orderId}`);
            const tracking = res.data;
            // Render tracking details in modal or new view
            api.showToast(`Số tracking: ${tracking.trackingId}`, "info");
            layout.render('Shipping', 'Tracking', orderId); // Render tracking page
        } catch (e) {
            api.showToast("Không thể tìm thấy thông tin vận chuyển cho đơn hàng này", "error");
        }
    },

    // 2. Order History
    loadHistory: async () => {
        try {
            const user = api.getUser();
            if (!user) {
                layout.render('Auth', 'Login');
                return;
            }
            const res = await api.get(`/orders/history?username=${user.username}`);
            const data = res.data || res;
            const container = $("#order-history-list");
            if (!container.length) return;
            container.empty();

            if (data.length === 0) {
                container.html('<tr><td colspan="6" class="text-center py-5 text-muted">Bạn chưa có đơn hàng nào.</td></tr>');
                return;
            }

            data.forEach(order => {
                const statusBadge = orders.getStatusBadge(order.trangThai);
                const canCancel = order.trangThai === 'MOI';
                const canTrack = order.trangThai === 'DANG_GIAO' || order.trangThai === 'HOAN_TAT';
                container.append(`
                    <tr>
                        <td class="fw-bold ps-4">#${order.maDonHang}</td>
                        <td>${formatOrderDate(order.ngayTao)}</td>
                        <td class="fw-bold text-accent">${api.formatCurrency(order.tongThanhToan)}</td>
                        <td>${order.maVoucher ? '<span class="badge bg-success-subtle text-success rounded-pill">Voucher: ' + order.maVoucher + '</span>' : '<span class="text-muted">---</span>'}</td>
                        <td>${statusBadge}</td>
                        <td class="text-end pe-4 d-flex gap-2 justify-content-end">
                            <button class="btn btn-sm btn-light rounded-pill px-3"
                                onclick="layout.render('Orders', 'Details', '${order.maDonHang}')"><i class="icon icon-document me-1"></i>Chi tiết</button>
                            <button class="btn btn-sm btn-outline-info rounded-pill px-3"
                                onclick="orders.trackOrder('${order.maDonHang}')"
                                ${!canTrack ? 'disabled title="Chỉ theo dõi khi đang giao/đã giao"' : ''}><i class="icon icon-map-pin me-1"></i>Theo dõi</button>
                            <button class="btn btn-sm btn-outline-danger rounded-pill px-3"
                                onclick="orders.cancelOrder('${order.maDonHang}')"
                                ${!canCancel ? 'disabled title="Chỉ có thể hủy đơn mới"' : ''}>Hủy</button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải lịch sử đơn hàng", "error");
        }
    },

    // Cancel an order (only allowed for MOI status)
    cancelOrder: async (orderId) => {
        if (!confirm("Bạn có chắc chắn muốn hủy đơn hàng #" + orderId + "?")) return;
        try {
            await api.put(`/orders/${orderId}/cancel`);
            api.showToast("Đã hủy đơn hàng thành công!");
            orders.loadHistory();
        } catch (e) {
            api.showToast("Không thể hủy đơn hàng này: " + e.message, "error");
        }
    },

    loadAdminOrders: async () => {
        try {
            const res = await api.get('/orders/admin');
            const data = res.data || res;
            const tbody = $("#orders-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            data.forEach(order => {
                tbody.append(`
                    <tr>
                        <td><span class="fw-bold">#${order.maDonHang}</span></td>
                        <td>
                            <div class="fw-bold">${order.hoTenKhachHang || order.username}</div>
                        </td>
                        <td>${formatOrderDate(order.ngayTao, true)}</td>
                        <td class="fw-bold text-accent">${api.formatCurrency(order.tongThanhToan)}</td>
                        <td>${orders.getStatusSelect(order.maDonHang, order.trangThai)}</td>
                        <td class="text-end">
                            <button class="btn btn-sm btn-light" onclick="layout.render('Orders', 'Details', '${order.maDonHang}')">Xem</button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách đơn hàng", "error");
        }
    },

    updateStatus: async (orderId, newStatus) => {
        try {
            await api.put(`/orders/admin/${orderId}/status?trangThai=${newStatus}`);
            api.showToast("Đã cập nhật trạng thái đơn hàng");
            orders.loadAdminOrders();
        } catch (e) {
            api.showToast("Lỗi khi cập nhật trạng thái", "error");
        }
    },

    getStatusBadge: (status) => {
        const maps = {
            'MOI':          '<span class="badge bg-warning text-dark">Mới / Chờ xác nhận</span>',
            'DA_XAC_NHAN':  '<span class="badge bg-info text-white">Đã xác nhận</span>',
            'CHO_LAY_HANG': '<span class="badge bg-primary">Chờ lấy hàng</span>',
            'DANG_GIAO':    '<span class="badge bg-primary">Đang giao</span>',
            'HOAN_TAT':     '<span class="badge bg-success">Đã giao hoàn tất</span>',
            'DA_HUY':       '<span class="badge bg-secondary">Đã hủy</span>'
        };
        return maps[status] || `<span class="badge bg-light text-dark">${status}</span>`;
    },

    getStatusSelect: (orderId, currentStatus) => {
        const statuses = [
            { id: 'MOI',          text: 'Mới / Chờ xác nhận' },
            { id: 'DA_XAC_NHAN',  text: 'Đã xác nhận' },
            { id: 'CHO_LAY_HANG', text: 'Chờ lấy hàng' },
            { id: 'DANG_GIAO',    text: 'Đang giao' },
            { id: 'HOAN_TAT',     text: 'Hoàn tất' },
            { id: 'DA_HUY',       text: 'Hủy đơn' }
        ];
        
        let options = '';
        statuses.forEach(s => {
            options += `<option value="${s.id}" ${s.id === currentStatus ? 'selected' : ''}>${s.text}</option>`;
        });

        return `<select class="form-select form-select-sm rounded-pill" onchange="orders.updateStatus('${orderId}', this.value)">${options}</select>`;
    },

    // 4. Load Order Detail (Invoice View)
    loadDetail: async (orderId) => {
        try {
            const res = await api.get(`/orders/${orderId}`);
            const order = res.data || res;

            // Populate invoice header
            $("#invoice-id").text("#" + order.maDonHang);
            $("#invoice-date").text(formatOrderDate(order.ngayTao));
            $("#invoice-payment").text(order.maVoucher ? `Voucher: ${order.maVoucher}` : '---');
            $("#invoice-status")
                .text(order.trangThai)
                .attr('class', `badge ${orders.getStatusBadgeClass(order.trangThai)}`);

            // Populate customer info
            $("#invoice-customer-name").text(order.hoTenKhachHang || order.username);
            $("#invoice-customer-phone").text('---');
            $("#invoice-customer-address").text(order.diaChiGiaoHang || '---');

            // Populate items
            const tbody = $("#invoice-items");
            tbody.empty();
            (order.chiTietDonHangs || []).forEach(item => {
                const lineTotal = item.giaBanChot * item.soLuong;
                tbody.append(`
                    <tr>
                        <td class="ps-4">${item.tenSach}</td>
                        <td class="text-center">${item.soLuong}</td>
                        <td class="text-end">${api.formatCurrency(item.giaBanChot)}</td>
                        <td class="pe-4 text-end fw-bold">${api.formatCurrency(lineTotal)}</td>
                    </tr>
                `);
            });

            // Use values from backend (already computed)
            $("#invoice-subtotal").text(api.formatCurrency(order.tongTienHang));
            $("#invoice-shipping").text(
                order.phiVanChuyen == 0 ? "Miễn phí" : api.formatCurrency(order.phiVanChuyen)
            );
            $("#invoice-total").text(api.formatCurrency(order.tongThanhToan));
        } catch (e) {
            api.showToast("Không thể tải chi tiết đơn hàng", "error");
        }
    },

    getStatusBadgeClass: (status) => {
        const map = {
            'MOI':          'bg-warning text-dark',
            'DA_XAC_NHAN':  'bg-info text-white',
            'CHO_LAY_HANG': 'bg-primary',
            'DANG_GIAO':    'bg-primary',
            'HOAN_TAT':     'bg-success',
            'DA_HUY':       'bg-secondary'
        };
        return map[status] || 'bg-secondary';
    }
};

$(document).on('click', '#btn-place-order', function() {
    orders.processCheckout();
});

$(document).on('click', '#btn-apply-voucher', function() {
    orders.applyVoucher();
});
