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
            const res = await api.get('/admin/accounts/get-users');
            const data = Array.isArray(res) ? res : (res.data || []);
            const tbody = $("#users-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            if (data.length === 0) {
                tbody.html('<tr><td colspan="6" class="text-center py-4 text-muted">No users found in system.</td></tr>');
                return;
            }

            const roleBadge = { ADMIN: 'bg-danger', STAFF: 'bg-primary', STOREKEEPER: 'bg-warning text-dark', CUSTOMER: 'bg-secondary' };

            data.forEach(user => {
                const isActive = user.isActive === true; // Aligned with AccountDTO isActive
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold text-dark">${user.username}</td>
                        <td>${user.fullName || '---'}</td>
                        <td><span class="badge ${roleBadge[user.role] || 'bg-secondary'} rounded-pill px-3">${user.role}</span></td>
                        <td>${user.createdAt ? new Date(user.createdAt).toLocaleDateString('en-GB') : '---'}</td>
                        <td><span class="badge ${isActive ? 'bg-success' : 'bg-danger'} bg-opacity-15 ${isActive ? 'text-success' : 'text-danger'} rounded-pill px-3">${isActive ? 'Active' : 'Locked'}</span></td>
                        <td class="text-end pe-4 d-flex gap-2 justify-content-end">
                            <select class="form-select form-select-sm rounded-pill" style="width: auto"
                                onchange="users.changeRole('${user.username}', this.value)">
                                ${['ADMIN','STAFF','STOREKEEPER','CUSTOMER'].map(r =>
                                    `<option value="${r}" ${r === user.role ? 'selected' : ''}>${r}</option>`
                                ).join('')}
                            </select>
                            <button class="btn btn-sm btn-outline-${isActive ? 'danger' : 'success'} rounded-pill px-3"
                                onclick="users.toggleStatus('${user.username}', ${isActive})">
                                ${isActive ? 'Lock' : 'Unlock'}
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Failed to load user list", "error");
        }
    },

    toggleStatus: async (username, currentActive) => {
        try {
            // Backend: PUT /admin/accounts/{username}/status?status=true/false
            await api.put(`/admin/accounts/${username}/status?status=${!currentActive}`);
            api.showToast("Account status updated successfully!");
            users.loadAdminList();
        } catch (e) {
            api.showToast("Error updating account status", "error");
        }
    },

    changeRole: async (username, newRole) => {
        try {
            await api.put(`/admin/accounts/${username}/role?role=${newRole}`);
            api.showToast(`Role updated to ${newRole}`);
        } catch (e) {
            api.showToast("Error updating user role", "error");
            users.loadAdminList();
        }
    },

    adminCreate: async () => {
        const username    = $("#new-username").val().trim();
        const password    = $("#new-password").val().trim();
        const role        = $("#new-role").val();
        const fullName    = $("#new-fullName").val().trim();
        const phone       = $("#new-phone").val().trim();

        if (!username || !password || !role) {
            api.showToast("Please fill in all required fields", "warning");
            return;
        }

        try {
            await api.post('/admin/accounts/create', { username, password, role, fullName, phone });
            api.showToast("Account created successfully!");
            users.loadAdminList();
            ["#new-username","#new-password","#new-fullName","#new-phone"].forEach(id => $(id).val(''));
        } catch (e) {
            api.showToast("Error creating account: " + e.message, "error");
        }
    },

    // =========================================================
    // CLIENT: Profile Management
    // =========================================================
    loadProfile: async () => {
        const user = api.getUser();
        if (!user) { layout.render('Auth', 'Login'); return; }

        try {
            const res = await api.get('/users/profile');
            const p = res.data || res;

            $("#profile-username").val(p.username || user.username);
            $("#profile-fullName").val(p.fullName || '');
            $("#profile-phone").val(p.phoneNumber || p.phone || '');
            $("#profile-address").val(p.address || '');
            $("#profile-email").val(p.email || '');
            $("#profile-role").val(p.role || user.role || '');
        } catch (e) {
            api.showToast("Failed to load profile information", "error");
        }
    },

    updateProfile: async () => {
        const user = api.getUser();
        if (!user) return;

        const isCustomer = user.role === 'CUSTOMER';
        const endpoint   = isCustomer ? '/users/profile/update-customer' : '/users/profile/update-staff';

        const data = {
            fullName:    $("#profile-fullName").val().trim(),
            phone:       $("#profile-phone").val().trim(),
            address:     isCustomer ? $("#profile-address").val().trim() : undefined,
        };

        try {
            await api.put(endpoint, data);
            api.showToast("Profile updated successfully!");
            // Update local storage if needed
            const current = api.getUser();
            if (current) {
                current.fullName = data.fullName;
                localStorage.setItem('user', JSON.stringify(current));
                layout.updateUserHeader();
            }
        } catch (e) {
            api.showToast("Error updating profile", "error");
        }
    },

    changePassword: async () => {
        const oldPass  = $("#pw-old").val().trim();
        const newPass  = $("#pw-new").val().trim();
        const confirm2 = $("#pw-confirm").val().trim();

        if (!oldPass || !newPass) {
            api.showToast("Please enter all password fields", "warning");
            return;
        }
        if (newPass !== confirm2) {
            api.showToast("Passwords do not match", "error");
            return;
        }
        if (newPass.length < 6) {
            api.showToast("New password must be at least 6 characters", "warning");
            return;
        }

        try {
            await api.put('/users/profile/change-password', { oldPassword: oldPass, newPassword: newPass });
            api.showToast("Password changed successfully!");
            ["#pw-old","#pw-new","#pw-confirm"].forEach(id => $(id).val(''));
        } catch (e) {
            api.showToast("Password change failed: " + e.message, "error");
        }
    }
};
