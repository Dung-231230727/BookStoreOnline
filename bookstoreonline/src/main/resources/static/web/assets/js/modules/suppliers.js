/**
 * suppliers.js - Supplier & Partner Management
 */
const suppliers = {
    supplierData: [],

    loadList: async () => {
        try {
            const res = await api.get('/admin/suppliers');
            const list = res.data.data ? res.data.data : (res.data || []);
            suppliers.supplierData = list;
            
            const tbody = $("#suppliers-list");
            if (!tbody.length) return;
            tbody.empty();

            if (list.length === 0) {
                tbody.html('<tr><td colspan="3" class="text-center py-4 text-muted">Không tìm thấy nhà cung cấp nào.</td></tr>');
                return;
            }

            list.forEach(s => {
                tbody.append(`
                    <tr>
                        <td class="ps-4">
                            <div class="fw-bold text-dark">${s.supplierName}</div>
                            <div class="extra-small text-muted">ID: ${s.supplierId}</div>
                        </td>
                        <td>
                            <div class="small"><i class="icon icon-map-pin me-1 opacity-50"></i>${s.contactInfo || 'Chưa cập nhật'}</div>
                        </td>
                        <td class="text-end pe-4">
                            <button onclick="suppliers.edit(${s.supplierId})" class="btn btn-sm btn-light rounded-pill px-3 me-1">
                                <i class="icon icon-edit me-1"></i> Sửa
                            </button>
                            <button onclick="suppliers.delete(${s.supplierId})" class="btn btn-sm btn-outline-danger rounded-pill px-3">
                                <i class="icon icon-trash me-1"></i> Xóa
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            console.error(e);
            api.showToast("Không thể tải danh sách nhà cung cấp", "error");
        }
    },

    addSupplier: () => {
        $("#supplierId").val('');
        $("#supplierName").val('');
        $("#supplierContactInfo").val('');
        
        $("#supplierModalTitle").text("Thêm Đối tác mới");
        const modal = new bootstrap.Modal(document.getElementById('supplierModal'));
        modal.show();
    },

    edit: (id) => {
        const s = suppliers.supplierData.find(item => item.supplierId === id);
        if (!s) return;

        $("#supplierId").val(s.supplierId);
        $("#supplierName").val(s.supplierName);
        $("#supplierContactInfo").val(s.contactInfo || '');

        $("#supplierModalTitle").text("Cập nhật thông tin Đối tác");
        const modal = new bootstrap.Modal(document.getElementById('supplierModal'));
        modal.show();
    },

    saveSupplier: async () => {
        const id = $("#supplierId").val();
        const data = {
            supplierName: $("#supplierName").val().trim(),
            contactInfo: $("#supplierContactInfo").val().trim() 
        };

        if (!data.supplierName) {
            api.showToast("Vui lòng nhập tên nhà cung cấp", "error");
            return;
        }

        try {
            if (id) {
                await api.put(`/admin/suppliers/${id}`, data);
                api.showToast("Đã cập nhật thông tin thành công!", "success");
            } else {
                await api.post('/admin/suppliers', data);
                api.showToast("Đã thêm đối tác mới thành công!", "success");
            }
            
            bootstrap.Modal.getInstance(document.getElementById('supplierModal')).hide();
            suppliers.loadList();
        } catch (e) {
            console.error(e);
            api.showToast("Lỗi lưu thông tin đối tác", "error");
        }
    },

    delete: async (id) => {
        if (!confirm("Bạn có chắc chắn muốn xóa đối tác này?")) return;
        try {
            await api.delete(`/admin/suppliers/${id}`);
            api.showToast("Đã xóa đối tác thành công", "success");
            suppliers.loadList();
        } catch (e) { 
            api.showToast("Lỗi: Không thể xóa đối tác (có thể đang có ràng buộc dữ liệu)", "error"); 
        }
    }
};