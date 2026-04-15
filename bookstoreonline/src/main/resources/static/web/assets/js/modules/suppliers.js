/**
 * suppliers.js - Supplier & Partner Management (Person 3)
 */
const suppliers = {
    loadList: async () => {
        try {
            const res = await api.get('/admin/suppliers');
            const data = res.data || res;
            const tbody = $("#suppliers-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            if (data.length === 0) {
                tbody.html('<tr><td colspan="4" class="text-center py-4 text-muted">Chưa đăng ký đối tác NPP/NCC nào.</td></tr>');
                return;
            }

            data.forEach(item => {
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold text-muted">#${item.maNcc}</td>
                        <td class="fw-bold text-dark">${item.tenNcc}</td>
                        <td>${item.soDienThoai} <br><small class="text-muted">${item.email || 'N/A'}</small></td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-outline-danger rounded-pill px-3" onclick="api.showToast('Giao diện chỉnh sửa đang khóa')">Sửa</button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            console.error("Lỗi tải NCC:", e);
        }
    },
    
    addSupplier: async () => {
         // Thêm NCC demo
         api.showToast("Đang kết nối API thêm Supplier (Auto mock)...", "info");
         const randomID = Date.now().toString().slice(-4);
         const dto = {
             tenNcc: "Đối Tác Xuất Bản Toàn Cầu " + randomID,
             diaChi: "Logistic Center, Book Street, HN",
             soDienThoai: "09" + randomID + randomID,
             email: "supplier." + randomID + "@booksaw.vn"
         };
         try {
             await api.post('/admin/suppliers', dto);
             api.showToast("Cập nhật đăng ký Nhà cung cấp thành công!");
             suppliers.loadList();
         } catch(e) {
             api.showToast("Không thể đăng ký: " + e.message, "error");
         }
    }
};
