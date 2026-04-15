/**
 * suppliers.js - Supplier & Partner Management
 */
const suppliers = {
    loadList: async () => {
        try {
            const res = await api.get('/admin/suppliers');
            const list = res.data || [];
            const tbody = $("#suppliers-list");
            if (!tbody.length) return;
            tbody.empty();

            list.forEach(s => {
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold">${s.name}</td>
                        <td>${s.taxCode || '---'}</td>
                        <td>${s.phone || '---'}</td>
                        <td class="text-end pe-4">
                            <button onclick="suppliers.edit(${s.id})" class="btn btn-sm btn-light">Edit</button>
                            <button onclick="suppliers.delete(${s.id})" class="btn btn-sm btn-outline-danger">Delete</button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) { console.error(e); }
    },

    delete: async (id) => {
        if (!confirm("Remove this supplier?")) return;
        try {
            await api.delete(`/admin/suppliers/${id}`);
            api.showToast("Supplier removed");
            suppliers.loadList();
        } catch (e) { api.showToast("Failed to remove supplier", "error"); }
    }
};
