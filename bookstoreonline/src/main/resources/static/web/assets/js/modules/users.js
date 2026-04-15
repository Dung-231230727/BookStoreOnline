/**
 * users.js - User Management Logic (Person 1)
 * Covers: GET/PUT /api/users/*, POST/GET/PUT /api/admin/users/*
 */
const users = {

    // =========================================================
    // ADMIN: User List Management
    // =========================================================
    loadAdminList: async () => {
        try {
            const res = await api.get('/admin/users/get-users');
            const data = Array.isArray(res) ? res : (res.data || []);
            const tbody = $("#users-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            if (data.length === 0) {
                tbody.html('<tr><td colspan="6" class="text-center py-4 text-muted">Không có người dùng nào.</td></tr>');
                return;
            }

            const roleBadge = { ADMIN: 'bg-danger', STAFF: 'bg-primary', STOREKEEPER: 'bg-warning text-dark', CUSTOMER: 'bg-secondary' };

            data.forEach(user => {
                const isActive = user.trangThai === true || user.trangThai === 1;
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold text-dark">${user.username}</td>
                        <td>${user.hoTen || '---'}</td>
                        <td><span class="badge ${roleBadge[user.role] || 'bg-secondary'} rounded-pill px-3">${user.role}</span></td>
                        <td>${user.ngayTao ? new Date(user.ngayTao).toLocaleDateString('vi-VN') : '---'}</td>
                        <td><span class="badge ${isActive ? 'bg-success' : 'bg-danger'} bg-opacity-15 ${isActive ? 'text-success' : 'text-danger'} rounded-pill px-3">${isActive ? 'Hoạt động' : 'Bị khóa'}</span></td>
                        <td class="text-end pe-4 d-flex gap-2 justify-content-end">
                            <select class="form-select form-select-sm rounded-pill" style="width: auto"
                                onchange="users.changeRole('${user.username}', this.value)">
                                ${['ADMIN','STAFF','STOREKEEPER','CUSTOMER'].map(r =>
                                    `<option value="${r}" ${r === user.role ? 'selected' : ''}>${r}</option>`
                                ).join('')}
                            </select>
                            <button class="btn btn-sm btn-outline-${isActive ? 'danger' : 'success'} rounded-pill px-3"
                                onclick="users.toggleStatus('${user.username}')">
                                ${isActive ? 'Khóa' : 'Mở khóa'}
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách người dùng", "error");
        }
    },

    toggleStatus: async (username) => {
        try {
            // Backend: PUT /admin/users/{username}/status?status=true/false
            // We need to know current status to toggle — reload and check
            const res = await api.get('/admin/users/get-users');
            const data = Array.isArray(res) ? res : (res.data || []);
            const user = data.find(u => u.username === username);
            const newStatus = user ? !(user.trangThai === true || user.trangThai === 1) : false;
            await api.put(`/admin/users/${username}/status?status=${newStatus}`);
            api.showToast("Cập nhật trạng thái thành công!");
            users.loadAdminList();
        } catch (e) {
            api.showToast("Lỗi khi khóa/mở khóa tài khoản", "error");
        }
    },

    changeRole: async (username, newRole) => {
        try {
            // Backend: PUT /admin/users/{username}/role?role=VALUE
            await api.put(`/admin/users/${username}/role?role=${newRole}`);
            api.showToast(`Đã thay đổi quyền thành ${newRole}`);
        } catch (e) {
            api.showToast("Lỗi khi thay đổi quyền", "error");
            users.loadAdminList();
        }
    },

    // ADMIN: Create new staff/admin account
    adminCreate: async () => {
        const username    = $("#new-username").val().trim();
        const password    = $("#new-password").val().trim();
        const role        = $("#new-role").val();
        const hoTen       = $("#new-hoten").val().trim();
        const sdt         = $("#new-sdt").val().trim();

        if (!username || !password || !role) {
            api.showToast("Vui lòng điền đầy đủ thông tin bắt buộc", "warning");
            return;
        }

        try {
            await api.post('/admin/users/create', { username, password, role, hoTen, sdt });
            api.showToast("Tạo tài khoản thành công!");
            users.loadAdminList();
            // Clear form
            ["#new-username","#new-password","#new-hoten","#new-sdt"].forEach(id => $(id).val(''));
        } catch (e) {
            api.showToast("Lỗi tạo tài khoản: " + e.message, "error");
        }
    },

    // =========================================================
    // CLIENT: Profile Management
    // =========================================================
    loadProfile: async () => {
        const user = api.getUser();
        if (!user) { layout.render('Auth', 'Login'); return; }

        try {
            const res = await api.get('/users/get-profile');
            const profile = res.data || res;

            $("#profile-username").val(profile.username || user.username);
            $("#profile-hoten").val(profile.hoTen || '');
            $("#profile-sdt").val(profile.sdt || '');
            $("#profile-diachi").val(profile.diaChiGiaoHang || '');
            $("#profile-email").val(profile.email || '');
            $("#profile-role").val(profile.role || user.role || '');
        } catch (e) {
            api.showToast("Không thể tải thông tin hồ sơ", "error");
        }
    },

    updateProfile: async () => {
        const user = api.getUser();
        if (!user) return;

        const isCustomer = user.role === 'CUSTOMER';
        const endpoint   = isCustomer ? '/users/update-customer-profile' : '/users/update-staff-profile';

        const data = {
            hoTen:            $("#profile-hoten").val().trim(),
            sdt:              $("#profile-sdt").val().trim(),
            diaChiGiaoHang:   isCustomer ? $("#profile-diachi").val().trim() : undefined,
        };

        try {
            await api.put(endpoint, data);
            api.showToast("Cập nhật hồ sơ thành công!");
        } catch (e) {
            api.showToast("Lỗi khi cập nhật hồ sơ", "error");
        }
    },

    changePassword: async () => {
        const oldPass  = $("#pw-old").val().trim();
        const newPass  = $("#pw-new").val().trim();
        const confirm2 = $("#pw-confirm").val().trim();

        if (!oldPass || !newPass) {
            api.showToast("Vui lòng điền đầy đủ mật khẩu", "warning");
            return;
        }
        if (newPass !== confirm2) {
            api.showToast("Xác nhận mật khẩu không khớp", "error");
            return;
        }
        if (newPass.length < 6) {
            api.showToast("Mật khẩu mới phải có ít nhất 6 ký tự", "warning");
            return;
        }

        try {
            await api.put('/users/update-password', { oldPassword: oldPass, newPassword: newPass });
            api.showToast("Đổi mật khẩu thành công!");
            ["#pw-old","#pw-new","#pw-confirm"].forEach(id => $(id).val(''));
        } catch (e) {
            api.showToast("Lỗi đổi mật khẩu: " + e.message, "error");
        }
    }
};
