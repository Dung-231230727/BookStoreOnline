/**
 * inventory.js - Logistics & Warehouse Management (Person 3)
 */
const inventory = {
    currentRawData: [],

    // 1. Load Inventory List with Low-Stock highlights
    loadList: async () => {
        try {
            const lowStockRes = await api.get('/inventory/low-stock');
            const lowStockData = Array.isArray(lowStockRes) ? lowStockRes : (lowStockRes.data || []);
            // Đếm số lượng low-stock
            $("#low-stock-count").text(lowStockData.length);

            // Tải chi tiết toàn bộ kho thông qua API Books (Vì Book Entity map trực tiếp số lượng tồn kho)
            const res = await api.get('/books');
            const data = Array.isArray(res) ? res : (res.data || []);
            
            inventory.currentRawData = data.map(item => {
                // Kiểm tra xem nó có trong list lowStock ko
                const isLowUrl = lowStockData.find(l => l.isbn === item.isbn);
                return { sach: item, isLowStock: !!isLowUrl };
            });

            // Update stats
            $("#total-inventory-items").text(data.length);
            
            // Tính tổng giá trị (demo dùng giá niêm yết nhân tồn kho)
            let totalValue = 0;
            data.forEach(i => totalValue += (i.giaNiemYet || 0) * (i.soLuongHienTai || 0));
            $("#total-inventory-value").text(api.formatCurrency(totalValue));

            inventory.renderTable(inventory.currentRawData);
        } catch (e) {
            console.error("Lỗi tải kho:", e);
        }
    },

    filterStock: (searchValue) => {
        const query = searchValue.toLowerCase();
        let filtered = inventory.currentRawData;
        if(query) {
             filtered = filtered.filter(row => 
                 row.sach.tenSach.toLowerCase().includes(query) || 
                 row.sach.isbn.toLowerCase().includes(query)
             );
        }
        // Also apply status filter
        const status = $("#inventory-filter").val();
        inventory.renderFiltered(filtered, status);
    },

    filterStatus: (status) => {
        inventory.renderFiltered(inventory.currentRawData, status);
    },

    renderFiltered: (dataList, status) => {
         let finalData = dataList;
         if (status === 'LOW_STOCK') {
             finalData = dataList.filter(d => d.isLowStock);
         } else if (status === 'IN_STOCK') {
             finalData = dataList.filter(d => !d.isLowStock);
         }
         inventory.renderTable(finalData);
    },

    renderTable: (data) => {
        const tbody = $("#inventory-admin-body");
        if (!tbody.length) return;
        tbody.empty();

        if (data.length === 0) {
            tbody.html('<tr><td colspan="5" class="text-center py-4 text-muted">Không có dữ liệu tồn kho</td></tr>');
            return;
        }

        data.forEach(item => {
            const bgClass = item.isLowStock ? 'bg-danger bg-opacity-10' : '';
            const statusBadge = item.isLowStock ? '<span class="badge bg-danger">Sắp hết hàng</span>' : '<span class="badge bg-success">Còn hàng</span>';
            const price = api.formatCurrency(item.sach.giaNiemYet);

            tbody.append(`
                <tr class="${bgClass}">
                    <td class="ps-4">
                        <div class="fw-bold text-dark">${item.sach.tenSach}</div>
                        <div class="small text-muted">ISBN: ${item.sach.isbn}</div>
                    </td>
                    <td class="text-center">
                        <span class="badge ${item.isLowStock ? 'bg-danger' : 'bg-dark'} rounded-pill px-3 fs-6">
                            ${item.sach.soLuongHienTai}
                        </span>
                    </td>
                    <td>
                        <div class="small fw-bold">Kệ A-12 (Mặc định)</div>
                        <div class="extra-small text-muted">Giá vốn tham chiếu: ${price}</div>
                    </td>
                    <td>${statusBadge} <br><span class="extra-small text-muted">Mức cảnh báo tồn tối thiểu: 10</span></td>
                    <td class="text-end pe-4">
                        <button onclick="api.showToast('Gắn chip / In bill cho SP')" class="btn btn-sm btn-outline-secondary rounded-pill">Kho Logic</button>
                    </td>
                </tr>
            `);
        });
    },

    // 2. Import Goods logic
    addImportRow: () => {
        const tbody = $("#import-items-table tbody");
        const rowId = Date.now();
        tbody.append(`
            <tr id="row-${rowId}" class="import-row">
                <td><input type="text" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-isbn fw-bold text-accent" placeholder="Nhập mã chuẩn (VD: 978...)" onblur="inventory.calcImportSubtotal()"></td>
                <td><input type="number" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-qty" value="10" min="1" onchange="inventory.calcImportSubtotal()"></td>
                <td><input type="number" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-price" value="50000" onchange="inventory.calcImportSubtotal()"></td>
                <td class="fw-bold text-accent d-subtotal align-middle">500,000đ</td>
                <td class="text-end align-middle"><button onclick="$('#row-${rowId}').remove(); inventory.calcImportSubtotal();" class="btn btn-link link-danger p-0"><i class="icon icon-close text-danger"></i></button></td>
            </tr>
        `);
        inventory.calcImportSubtotal();
    },

    calcImportSubtotal: () => {
        let total = 0;
        $(".import-row").each(function() {
             const qtyStr = $(this).find('.d-qty').val();
             const priceStr = $(this).find('.d-price').val();
             const qty = parseInt(qtyStr) || 0;
             const price = parseFloat(priceStr) || 0;
             const sub = qty * price;
             $(this).find('.d-subtotal').text(api.formatCurrency(sub));
             total += sub;
        });
        $("#import-total-value").text(api.formatCurrency(total));
    },

    initImportForm: async () => {
        $("#import-items-table tbody").empty();
        inventory.addImportRow();
        
        // Load suppliers for dropdown
        try {
             const res = await api.get('/admin/suppliers');
             const suppliersData = res.data || res;
             const sel = $("#supplier-select");
             sel.empty().append('<option value="">--- Chọn Đơn vị Logistic / NCC ---</option>');
             suppliersData.forEach(s => {
                  sel.append(`<option value="${s.maNcc}">${s.tenNcc}</option>`);
             });
        } catch(e) {}
    },

    processImport: async () => {
        const maNcc = $("#supplier-select").val();
        if (!maNcc) {
             api.showToast("Kho Cảnh báo: Phải gán phiếu với Nhà cung cấp/Nhà phát hành!", "warning");
             return;
        }

        const items = [];
        $(".import-row").each(function() {
             const isbn = $(this).find('.d-isbn').val()?.trim();
             const soLuong = parseInt($(this).find('.d-qty').val()) || 0;
             const donGiaNhap = parseFloat($(this).find('.d-price').val()) || 0;
             if (isbn && soLuong > 0 && donGiaNhap > 0) {
                 items.push({ isbn, soLuong, donGiaNhap });
             }
        });

        if(items.length === 0) {
             api.showToast("Biểu mẫu trắng! Vui lòng nhập thông tin sách", "error"); return;
        }

        const dto = {
             maNcc: maNcc,
             ghiChu: $("#import-note").val() + " REF-" + $("#import-ref").val(),
             chiTiet: items
        };

        api.showToast("Bắt đầu khởi tạo Transaction nhập kho...", "info");
        try {
             const res = await api.post('/inventory/import', dto);
             const successId = (res.data && res.data.maPhieuNhap) ? res.data.maPhieuNhap : 'Auto-Gen';
             api.showToast("✓ Lô hàng đã ghi nhận thành công, Phiếu GD: #" + successId);
             
             // Phóng kho lại màn hình index
             setTimeout(() => { layout.render('Inventory/Admin', 'Index'); }, 1500);
        } catch(e) {
             api.showToast("Lỗi rò rỉ hoặc thiếu ISBN: " + e.message, "error");
        }
    },

    // 3. Export Goods logic (Automatic logic check)
    processExport: async () => {
        const orderId = $("#export-order-id").val()?.trim();
        if(!orderId) {
            api.showToast("Vui lòng gõ mã Đơn đặt hàng (VD: 3) để kích hoạt quét kho.", "warning"); return;
        }

        const logView = $("#export-result-log");
        logView.append(`<br><br>> Đang truy xuất Đơn hàng số: <b>${orderId}</b>...`);

        try {
             // DTO gửi đi gồm maDonHang, ghiChu
             const dto = { maDonHang: parseInt(orderId), ghiChu: "Xuất kho tự động qua Admin Panel (Auto Logistic)" };
             const res = await api.post('/inventory/export', dto);
             logView.removeClass("text-muted text-danger text-warning").addClass("text-success fw-bold");
             logView.append(`<br>> ✓ Quét kho và đối soát số lượng: HỢP LỆ (Status 200).`);
             logView.append(`<br>> ✓ Trừ tồn kho tự động: THÀNH CÔNG (Transaction OK).`);
             api.showToast("Hàng hóa đã được cấn trừ ra khỏi kho!");
        } catch(e) {
             logView.removeClass("text-muted text-success text-warning").addClass("text-danger fw-bold");
             logView.append(`<br>> ✗ KIỂM TRA THẤT BẠI (ROLLBACK). Lỗi báo về ERP: ${e.message}`);
             api.showToast("Sự cố vận hành xuất kho", "error");
        }
    },

    // 4. Shipping Tracking Logic
    trackOrder: async (trackingId) => {
        if (!trackingId) return;
        api.showToast("Đang thu thập dữ liệu vệ tinh GPS...", "info");
        try {
            // Lấy api Tracking (TrackingResponseDto)
            const res = await api.get(`/shipping/track/${trackingId}`);
            const data = res.data || res;
            if (data && data.maVanDon) {
                $("#tracking-result-container").fadeIn(400);
                $("#tracking-display-id").text("ID Bưu cục: " + data.maVanDon);
                
                // Set trạng thái cuối cùng
                const latestStatus = data.lichSuTrangThai[data.lichSuTrangThai.length - 1]?.trangThai || 'Khởi tạo mã';
                $("#tracking-status-badge").text(latestStatus);
                
                inventory.renderTrackingTimeline(data);
                api.showToast("Khoanh vùng thời gian thực thành công!", "success");
            }
        } catch (e) {
            api.showToast("Không tìm thấy mã vận đơn trên hệ thống Logistic", "error");
            $("#tracking-result-container").hide();
        }
    },

    renderTrackingTimeline: (data) => {
        const container = $("#tracking-timeline");
        if (!container.length) return;
        container.empty();
        
        let html = "";
        data.lichSuTrangThai.forEach((step, index) => {
            const isLast = index === (data.lichSuTrangThai.length - 1);
            const isFirst = index === 0;
            const timeStr = step.thoiGian ? new Date(step.thoiGian).toLocaleString('vi-VN', {hour12:true}) : 'Dữ liệu chìm';
            html += `
                <div class="timeline-item pb-4 border-start border-2 ${isLast ? 'border-accent' : 'border-light'} ps-4 position-relative">
                    <span class="position-absolute translate-middle-x ${isLast ? 'bg-accent shadow p-1 border border-accent border-4' : 'bg-light border'} rounded-circle" 
                          style="width:${isLast ? '18px':'14px'}; height:${isLast ? '18px':'14px'}; left:0; top:0; z-index:2;"></span>
                    <div class="small fw-bold ${isLast ? 'text-accent fs-6' : 'text-dark'}">${step.trangThai}</div>
                    <div class="small text-muted mb-1">${timeStr}</div>
                    <div class="extra-small opacity-75 fw-medium"><i class="icon icon-${isFirst ? 'store' : 'map-pin'} me-1 text-muted"></i> Cập nhật tại: ${step.viTri}</div>
                </div>
            `;
        });
        container.html(html);
    }
};
