/**
 * hoTro.js - Support Ticket Management
 */

const hoTro = {
    loadList: async () => {
        try {
            const user = api.getUser();
            if (!user) {
                layout.render('Auth', 'Login');
                return;
            }
            
            const res = await api.get(`/support?username=${user.username}`);
            const tickets = res.data || res;
            const tbody = $("#support-tickets-list");
            if (!tbody.length) return;
            tbody.empty();

            if (tickets.length === 0) {
                tbody.html('<tr><td colspan="4" class="text-center py-5 text-muted">Bạn chưa có yêu cầu hỗ trợ nào.</td></tr>');
                return;
            }

            tickets.forEach(ticket => {
                const statusBadge = hoTro.getStatusBadge(ticket.trangThai);
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold">${ticket.tieuDe}</td>
                        <td>${new Date(ticket.thoiGian).toLocaleDateString('vi-VN')}</td>
                        <td>${statusBadge}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-pill px-3" 
                                onclick="hoTro.viewDetail(${ticket.maHoTro})">
                                Xem chi tiết
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách hỗ trợ", "error");
        }
    },

    getStatusBadge: (status) => {
        const badges = {
            'OPEN': '<span class="badge bg-warning text-dark">Mở</span>',
            'PROCESSING': '<span class="badge bg-info text-white">Đang xử lý</span>',
            'CLOSED': '<span class="badge bg-success">Đã đóng</span>'
        };
        return badges[status] || `<span class="badge bg-secondary">${status}</span>`;
    },

    openCreateForm: () => {
        $("#hoTro-modal-title").text("Gửi yêu cầu hỗ trợ");
        $("#hoTro-form")[0].reset();
        const modal = new bootstrap.Modal(document.getElementById('hoTro-modal'));
        modal.show();
    },

    submitTicket: async () => {
        const user = api.getUser();
        if (!user) {
            api.showToast("Vui lòng đăng nhập trước", "error");
            return;
        }

        const ticketData = {
            username: user.username,
            tieuDe: $("#hoTro-title").val(),
            noiDung: $("#hoTro-content").val()
        };

        try {
            await api.post('/support/create', ticketData);
            api.showToast("Gửi yêu cầu hỗ trợ thành công! Chúng tôi sẽ phản hồi sớm.");
            const modal = bootstrap.Modal.getInstance(document.getElementById('hoTro-modal'));
            modal.hide();
            hoTro.loadList();
        } catch (e) {
            api.showToast("Lỗi gửi yêu cầu: " + e.message, "error");
        }
    },

    viewDetail: async (ticketId) => {
        try {
            const res = await api.get(`/support/${ticketId}`);
            const ticket = res.data;
            console.log("Support ticket detail:", ticket);
            api.showToast(`Yêu cầu: ${ticket.tieuDe}\nTrạng thái: ${ticket.trangThai}`, "info");
        } catch (e) {
            api.showToast("Không thể tải chi tiết yêu cầu", "error");
        }
    }
};

// Event handlers
$(document).on('click', '#btn-submit-hoTro', function() {
    hoTro.submitTicket();
});
