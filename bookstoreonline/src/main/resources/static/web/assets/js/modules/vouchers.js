/**
 * vouchers.js - Discount & Voucher Management
 */
const vouchers = {
    loadAdminList: async () => {
        try {
            const res = await api.get('/admin/vouchers'); // Check endpoint in VoucherController
            const list = res.data || [];
            const tbody = $("#vouchers-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            list.forEach(v => {
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold text-accent">${v.voucherCode}</td>
                        <td>${v.discountValue}%</td>
                        <td>${common.formatDate(v.expiryDate)}</td>
                        <td>${v.usageLimit || 'Unlimited'}</td>
                        <td class="text-end pe-4">
                            <button onclick="vouchers.delete('${v.voucherCode}')" class="btn btn-sm btn-outline-danger">Delete</button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) { console.error(e); }
    },

    delete: async (code) => {
        if (!confirm("Delete this voucher?")) return;
        try {
            await api.delete(`/admin/vouchers/${code}`);
            api.showToast("Voucher deleted");
            vouchers.loadAdminList();
        } catch (e) { api.showToast("Failed to delete voucher", "error"); }
    }
};
