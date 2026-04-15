/**
 * auditlog.js - Audit Log & System Activity Tracking
 */

const auditlog = {
    /**
     * Load audit logs with filters
     */
    loadLogs: async () => {
        try {
            const search = $("#audit-search").val() || "";
            const action = $("#audit-action-filter").val() || "";
            // Adjusted params to match Backend expectations if any, often using QueryParams
            const res = await api.get(`/admin/audit-logs`); 
            const logs = res.data || res;
            const tbody = $("#audit-list-body");
            if (!tbody.length) return;
            tbody.empty();

            if (!logs || logs.length === 0) {
                tbody.html('<tr><td colspan="7" class="text-center py-5 text-muted">No system activities found.</td></tr>');
                return;
            }

            logs.forEach(log => {
                const actionBadge = auditlog.getActionBadge(log.action);
                tbody.append(`
                    <tr onclick="auditlog.viewDetail('${log.id}')">
                        <td>${log.timestamp || '---'}</td>
                        <td><strong>${log.username || '---'}</strong></td>
                        <td>${actionBadge}</td>
                        <td title="${log.details}" class="text-truncate" style="max-width: 300px;">${log.details || '--'}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-circle" onclick="event.stopPropagation(); auditlog.viewDetail('${log.id}')">
                                <i class="icon icon-arrow-right"></i>
                            </button>
                        </td>
                    </tr>
                `);
            });

            // Update statistics
            auditlog.updateStatistics();
        } catch (e) {
            api.showToast("Failed to load audit logs: " + e.message, "error");
        }
    },

    /**
     * View audit log detail
     */
    viewDetail: async (logId) => {
        try {
            const res = await api.get(`/admin/audit-logs/${logId}`);
            const log = res.data;

            console.log("Audit log detail:", log);
            
            // In a real scenario, this would populate a modal
            api.showToast("Log details printed to console", "info");
        } catch (e) {
            api.showToast("Error loading detail: " + e.message, "error");
        }
    },

    /**
     * Update statistics
     */
    updateStatistics: async () => {
        try {
            const res = await api.get(`/admin/audit-stats`);
            const stats = res.data;

            $("#audit-total-count").text(stats.totalLogs || 0);
            $("#audit-today-count").text(stats.loginCount || 0);
            $("#audit-last-scan").text(stats.lastScan || '---');
        } catch (e) {
            console.error("Stats load error:", e);
        }
    },

    /**
     * Get action badge HTML - Aligned with AuditAction.java Enum
     */
    getActionBadge: (action) => {
        const badges = {
            'CREATE_BOOK':     '<span class="badge bg-success text-white">Create Book</span>',
            'UPDATE_BOOK':     '<span class="badge bg-info text-white">Update Book</span>',
            'DELETE_BOOK':     '<span class="badge bg-danger text-white">Delete Book</span>',
            'CREATE_CATEGORY': '<span class="badge bg-success text-white">New Category</span>',
            'UPDATE_ORDER_STATUS': '<span class="badge bg-primary text-white">Order Status</span>',
            'LOGIN':           '<span class="badge bg-dark text-white">Login</span>',
            'CHANGE_PASSWORD': '<span class="badge bg-warning text-dark">Password</span>',
            'STOCK_UPDATE':    '<span class="badge bg-secondary text-white">Stock</span>'
        };
        
        return badges[action] || `<span class="badge bg-light text-dark border">${action || 'Activity'}</span>`;
    }
};

/**
 * Format date and time
 */
function formatDateTime(timestamp) {
    return new Date(timestamp).toLocaleString('vi-VN');
}

/**
 * Format currency to VND
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);
}
