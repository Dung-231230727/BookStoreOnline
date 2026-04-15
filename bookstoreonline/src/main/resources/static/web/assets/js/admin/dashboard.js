/**
 * dashboard.js - Admin Dashboard Logic
 * Aligned with AdminDashboardController.java:
 *   GET /api/admin/dashboard/revenue → RevenueReportDTO { totalRevenue, totalOrders, statusCheck }
 *   GET /api/admin/dashboard/ranking → List<BookRankingDTO> { isbn, tenSach, totalSold }
 *   GET /api/admin/audit-logs        → List<AuditLogDTO> { id, username, hanhDong, chiTiet, thoiDiem (String) }
 */
const dashboard = {
    init: () => {
        dashboard.loadStats();
        dashboard.loadRanking();
        dashboard.loadAuditLogs();
    },

    // 1. Revenue & KPI Stats
    loadStats: async () => {
        try {
            const res = await api.get('/admin/dashboard/revenue');
            const stats = res.data || res;
            if (!stats) return;

            // RevenueReportDTO only has totalRevenue and totalOrders
            const statRevEl  = document.getElementById('stat-revenue');
            const statOrdEl  = document.getElementById('stat-orders');

            if (statRevEl)  statRevEl.innerText  = api.formatCurrency(stats.totalRevenue  || 0);
            if (statOrdEl)  statOrdEl.innerText  = stats.totalOrders   || 0;

            // totalBooks & totalCustomers not in DTO → show placeholder
            const statBooksEl = document.getElementById('stat-books');
            const statCustEl  = document.getElementById('stat-customers');
            if (statBooksEl)  statBooksEl.innerText  = '---';
            if (statCustEl)   statCustEl.innerText   = '---';
        } catch (error) {
            console.error('Dashboard Stats Error:', error);
        }
    },

    // 2. Top Selling Books — BookRankingDTO uses 'totalSold' not 'soLuongBan'
    loadRanking: async () => {
        try {
            const res = await api.get('/admin/dashboard/ranking');
            const data = res.data || [];
            const tbody = document.getElementById('ranking-list');
            if (!tbody) return;
            tbody.innerHTML = '';

            if (!data || data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">Chưa có dữ liệu xếp hạng</td></tr>';
                return;
            }

            data.forEach((item, i) => {
                tbody.innerHTML += `
                    <tr>
                        <td class="ps-3">
                            <span class="badge ${i < 3 ? 'bg-warning text-dark' : 'bg-light text-dark'} rounded-pill">#${i + 1}</span>
                        </td>
                        <td class="fw-bold">${item.tenSach || item.isbn}</td>
                        <td class="text-end pe-3 fw-bold text-accent">${item.totalSold || 0} quyển</td>
                    </tr>
                `;
            });
        } catch (e) {
            console.error('Ranking Error:', e);
            const tbody = document.getElementById('ranking-list');
            if (tbody) tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">Không thể tải dữ liệu</td></tr>';
        }
    },

    // 3. Audit Logs — thoiDiem is already a formatted String ("dd/MM/yyyy HH:mm:ss"), no new Date() needed
    loadAuditLogs: async () => {
        try {
            const res = await api.get('/admin/audit-logs');
            const data = res.data || [];
            const tbody = document.getElementById('audit-log-list');
            if (!tbody) return;
            tbody.innerHTML = '';

            if (!data || data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">Chưa có nhật ký hệ thống</td></tr>';
                return;
            }

            data.slice(0, 20).forEach(log => {
                tbody.innerHTML += `
                    <tr>
                        <td class="ps-3 fw-bold text-dark">${log.username || '---'}</td>
                        <td class="text-wrap">${log.chiTiet || log.hanhDong || '---'}</td>
                        <td class="text-end pe-3 text-muted small text-nowrap">${log.thoiDiem || '---'}</td>
                    </tr>
                `;
            });
        } catch (e) {
            console.error('Audit Log Error:', e);
            const tbody = document.getElementById('audit-log-list');
            if (tbody) tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">Không thể tải nhật ký</td></tr>';
        }
    }
};
