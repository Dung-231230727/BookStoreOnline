/**
 * shipping.js - Shipping & Logistics Management
 */

const shipping = {
    /**
     * Load customer shipping list
     */
    loadList: async () => {
        try {
            const user = api.getUser();
            if (!user) {
                layout.render('Auth', 'Login');
                return;
            }
            
            const res = await api.get(`/vanchuyen?username=${user.username}`);
            const shipments = res.data || res;
            const tbody = $("#shipping-list-body");
            if (!tbody.length) return;
            tbody.empty();

            if (shipments.length === 0) {
                tbody.html('<tr><td colspan="6" class="text-center py-5 text-muted">Bạn chưa có vận chuyển nào.</td></tr>');
                return;
            }

            shipments.forEach(shipment => {
                const statusBadge = shipping.getStatusBadge(shipment.trangThai);
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold">#${shipment.maVanChuyen}</td>
                        <td>#${shipment.maDonHang}</td>
                        <td>${shipment.diaChiGiao}</td>
                        <td>${shipment.nhaCungUng}</td>
                        <td>${statusBadge}</td>
                        <td>${new Date(shipment.thoiGianTao).toLocaleDateString('vi-VN')}</td>
                        <td class="text-end pe-4">
                            <a href="javascript:void(0)" class="btn btn-sm btn-light rounded-pill px-3" 
                                onclick="layout.render('Shipping', 'Tracking')">
                                Theo dõi
                            </a>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách vận chuyển: " + e.message, "error");
        }
    },

    /**
     * Load admin shipments list with filters
     */
    loadAdminShipments: async () => {
        try {
            const search = $("#shipping-search").val() || "";
            const status = $("#shipping-status-filter").val() || "";
            const carrier = $("#shipping-carrier-filter").val() || "";

            const res = await api.get(`/vanchuyen`, { search, status, carrier });
            const shipments = res.data || res;
            const tbody = $("#shipping-list-body");
            if (!tbody.length) return;
            tbody.empty();

            if (!shipments || shipments.length === 0) {
                tbody.html('<tr><td colspan="7" class="text-center py-5 text-muted">Không tìm thấy vận chuyển nào.</td></tr>');
                return;
            }

            shipments.forEach(shipment => {
                const statusBadge = shipping.getStatusBadge(shipment.trangThai);
                tbody.append(`
                    <tr onclick="layout.render('Shipping/Admin', 'Details')">
                        <td class="ps-4 fw-bold">#${shipment.maVanChuyen}</td>
                        <td>#${shipment.maDonHang}</td>
                        <td>${shipment.diaChiGiao}</td>
                        <td>${shipment.nhaCungUng}</td>
                        <td>${statusBadge}</td>
                        <td>${new Date(shipment.thoiGianTao).toLocaleDateString('vi-VN')}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-circle" onclick="layout.render('Shipping/Admin', 'Details')">
                                <i class="icon icon-arrow-right"></i>
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách vận chuyển: " + e.message, "error");
        }
    },

    /**
     * Load shipping details
     */
    loadAdminDetails: async (shippingId) => {
        try {
            const res = await api.get(`/vanchuyen/${shippingId}`);
            const shipment = res.data;

            $("#shipping-id").text("#" + shipment.maVanChuyen);
            $("#shipping-order-id").text("#" + shipment.maDonHang);
            $("#shipping-carrier").text(shipment.nhaCungUng);
            $("#shipping-tracking-code").text(shipment.maTheoDoi);
            $("#shipping-receiver-name").text(shipment.tenNguoiNhan);
            $("#shipping-receiver-phone").text(shipment.dienThoai);
            $("#shipping-receiver-address").text(shipment.diaChiGiao);
            $("#shipping-status-badge").text(shipment.trangThai).className = 
                `badge badge-${shipping.getStatusClass(shipment.trangThai)} rounded-pill px-3 py-2`;
            $("#shipping-created-date").text(new Date(shipment.thoiGianTao).toLocaleDateString('vi-VN'));
            $("#shipping-weight").text((shipment.canNang || 0) + " kg");
            $("#shipping-fee").text(formatCurrency(shipment.phiVanChuyen));
            $("#shipping-new-status").val(shipment.trangThai);

            // Set GHN tracking link
            if (shipment.nhaCungUng === "GHN") {
                $("#ghn-link").attr("href", `https://khachhang.ghn.vn/tracking/${shipment.maTheoDoi}`);
            }

            api.showToast("Tải chi tiết vận chuyển thành công", "success");
        } catch (e) {
            api.showToast("Lỗi tải chi tiết vận chuyển: " + e.message, "error");
        }
    },

    /**
     * Update shipping status
     */
    updateStatus: async (event) => {
        event.preventDefault();
        try {
            const shippingId = new URLSearchParams(window.location.search).get('shippingId');
            const status = $("#shipping-new-status").val();
            const note = $("#shipping-update-note").val();

            const res = await api.put(`/vanchuyen/${shippingId}`, {
                trangThai: status,
                ghiChu: note
            });

            api.showToast("Cập nhật trạng thái vận chuyển thành công!", "success");
            setTimeout(() => {
                shipping.loadAdminDetails(shippingId);
            }, 1000);
        } catch (e) {
            api.showToast("Lỗi cập nhật trạng thái: " + e.message, "error");
        }
    },

    /**
     * Get status badge HTML
     */
    getStatusBadge: (status) => {
        const badges = {
            'PENDING': '<span class="badge bg-warning text-dark">Chờ xác nhận</span>',
            'IN_TRANSIT': '<span class="badge bg-info text-white">Đang giao</span>',
            'DELIVERED': '<span class="badge bg-success">Đã giao</span>',
            'RETURNED': '<span class="badge bg-danger">Trả lại</span>'
        };
        return badges[status] || `<span class="badge bg-secondary">${status}</span>`;
    },

    /**
     * Get status CSS class
     */
    getStatusClass: (status) => {
        const classes = {
            'PENDING': 'warning',
            'IN_TRANSIT': 'info',
            'DELIVERED': 'success',
            'RETURNED': 'danger'
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
