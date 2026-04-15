/**
 * inventory.js - Logistics & Warehouse Management
 * Standardized for Full English Backend synchronization (Hybrid Layer: Admin English)
 */
const inventory = {
    currentRawData: [],

    // 1. Load Inventory List with Low-Stock highlights
    loadList: async () => {
        try {
            const lowStockRes = await api.get('/inventory/low-stock');
            const lowStockData = lowStockRes.data || lowStockRes || [];
            
            $("#low-stock-count").text(lowStockData.length);

            // Fetch current stock from primary Books API
            const res = await api.get('/books');
            const data = res.data || res || [];
            
            inventory.currentRawData = data.map(item => {
                const isLow = lowStockData.find(l => l.isbn === item.isbn);
                return { book: item, isLowStock: !!isLow };
            });

            $("#total-inventory-items").text(data.length);
            
            // Calculate total inventory value
            let totalValue = 0;
            data.forEach(i => totalValue += (i.price || 0) * (i.stockQuantity || 0));
            $("#total-inventory-value").text(api.formatCurrency(totalValue));

            inventory.renderTable(inventory.currentRawData);
        } catch (e) {
            console.error("Inventory load error:", e);
        }
    },

    filterStock: (searchValue) => {
        const query = searchValue.toLowerCase();
        let filtered = inventory.currentRawData;
        if(query) {
             filtered = filtered.filter(row => 
                 row.book.title.toLowerCase().includes(query) || 
                 row.book.isbn.toLowerCase().includes(query)
             );
        }
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
            tbody.html('<tr><td colspan="5" class="text-center py-4 text-muted">No inventory data available</td></tr>');
            return;
        }

        data.forEach(item => {
            const bgClass = item.isLowStock ? 'bg-danger bg-opacity-10' : '';
            const statusBadge = item.isLowStock ? '<span class="badge bg-danger">Low Stock</span>' : '<span class="badge bg-success">In Stock</span>';
            const price = api.formatCurrency(item.book.price);

            tbody.append(`
                <tr class="${bgClass}">
                    <td class="ps-4">
                        <div class="fw-bold text-dark">${item.book.title}</div>
                        <div class="small text-muted">ISBN: ${item.book.isbn}</div>
                    </td>
                    <td class="text-center">
                        <span class="badge ${item.isLowStock ? 'bg-danger' : 'bg-dark'} rounded-pill px-3 fs-6">
                            ${item.book.stockQuantity || 0}
                        </span>
                    </td>
                    <td>
                        <div class="small fw-bold">Shelf A-12 (Default)</div>
                        <div class="extra-small text-muted">Reference Price: ${price}</div>
                    </td>
                    <td>${statusBadge} <br><span class="extra-small text-muted">Min Threshold: 5</span></td>
                    <td class="text-end pe-4">
                        <button onclick="api.showToast('Chip / Print barcode for SP')" class="btn btn-sm btn-outline-secondary rounded-pill">Logic Warehouse</button>
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
                <td><input type="text" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-isbn fw-bold text-accent" placeholder="Enter ISBN" onblur="inventory.calcImportSubtotal()"></td>
                <td><input type="number" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-qty" value="10" min="1" onchange="inventory.calcImportSubtotal()"></td>
                <td><input type="number" class="form-control form-control-sm border-0 bg-light rounded-pill px-3 d-unitPrice" value="50000" onchange="inventory.calcImportSubtotal()"></td>
                <td class="fw-bold text-accent d-subtotal align-middle">500,000đ</td>
                <td class="text-end align-middle"><button onclick="$('#row-${rowId}').remove(); inventory.calcImportSubtotal();" class="btn btn-link link-danger p-0"><i class="icon icon-close text-danger"></i></button></td>
            </tr>
        `);
        inventory.calcImportSubtotal();
    },

    calcImportSubtotal: () => {
        let total = 0;
        $(".import-row").each(function() {
             const qty = parseInt($(this).find('.d-qty').val()) || 0;
             const price = parseFloat($(this).find('.d-unitPrice').val()) || 0;
             const sub = qty * price;
             $(this).find('.d-subtotal').text(api.formatCurrency(sub));
             total += sub;
        });
        $("#import-total-value").text(api.formatCurrency(total));
    },

    initImportForm: async () => {
        $("#import-items-table tbody").empty();
        inventory.addImportRow();
        
        try {
             const res = await api.get('/admin/suppliers');
             const suppliersData = res.data || res || [];
             const sel = $("#supplier-select");
             sel.empty().append('<option value="">--- Select Supplier ---</option>');
             suppliersData.forEach(s => {
                  sel.append(`<option value="${s.id}">${s.name}</option>`);
             });
        } catch(e) {}
    },

    processImport: async () => {
        const supplierId = $("#supplier-select").val();
        if (!supplierId) {
             api.showToast("Warehouse Alert: Supplier selection required!", "warning");
             return;
        }

        const items = [];
        $(".import-row").each(function() {
             const isbn = $(this).find('.d-isbn').val()?.trim();
             const quantity = parseInt($(this).find('.d-qty').val()) || 0;
             const unitPrice = parseFloat($(this).find('.d-unitPrice').val()) || 0;
             if (isbn && quantity > 0 && unitPrice > 0) {
                 items.push({ isbn, quantity, unitPrice });
             }
        });

        if(items.length === 0) {
             api.showToast("Empty form! Please enter book details", "error"); return;
        }

        const dto = {
             supplierId: parseInt(supplierId),
             staffId: api.getUser()?.id || 1, // Fallback to 1 for now
             details: items
        };

        api.showToast("Initiating Import Transaction...", "info");
        try {
             const res = await api.post('/inventory/import', dto);
             api.showToast("✓ Batch recorded successfully! Internal ID: #" + (res.data?.id || 'OK'));
             setTimeout(() => { layout.render('Inventory/Admin', 'Index'); }, 1500);
        } catch(e) {
             api.showToast("Import failed: " + e.message, "error");
        }
    },

    // 3. Export Goods logic
    processExport: async () => {
        const orderId = $("#export-order-id").val()?.trim();
        if(!orderId) {
            api.showToast("Please enter Order ID to track.", "warning"); return;
        }

        const logView = $("#export-result-log");
        logView.append(`<br><br>> Checking Order ID: <b>${orderId}</b>...`);

        try {
             const dto = { orderId: orderId, note: "Auto-logistic export via Admin Dashboard" };
             await api.post('/inventory/export', dto);
             logView.removeClass("text-muted text-danger text-warning").addClass("text-success fw-bold");
             logView.append(`<br>> ✓ Stock verification: VALID.`);
             logView.append(`<br>> ✓ Inventory deducted: SUCCESS.`);
             api.showToast("Goods have been successfully deducted from inventory!");
        } catch(e) {
             logView.removeClass("text-muted text-success text-warning").addClass("text-danger fw-bold");
             logView.append(`<br>> ✗ CHECK FAILED (ROLLBACK). Error: ${e.message}`);
             api.showToast("Warehouse operational failure", "error");
        }
    },

    // 4. Shipping Tracking Logic
    trackOrder: async (trackingId) => {
        if (!trackingId) return;
        api.showToast("Collecting GPS tracking data...", "info");
        try {
            const res = await api.get(`/shipping/track/${trackingId}`);
            const data = res.data || res;
            if (data && data.trackingNumber) {
                $("#tracking-result-container").fadeIn(400);
                $("#tracking-display-id").text("Tracking #: " + data.trackingNumber);
                
                const latestStatus = data.statusHistory[data.statusHistory.length - 1]?.status || 'Initialized';
                $("#tracking-status-badge").text(latestStatus);
                
                inventory.renderTrackingTimeline(data);
                api.showToast("Real-time tracking successful!", "success");
            }
        } catch (e) {
            api.showToast("Tracking number not found in Logistic system", "error");
            $("#tracking-result-container").hide();
        }
    },

    renderTrackingTimeline: (data) => {
        const container = $("#tracking-timeline");
        if (!container.length) return;
        container.empty();
        
        let html = "";
        data.statusHistory.forEach((step, index) => {
            const isLast = index === (data.statusHistory.length - 1);
            const timeStr = step.timestamp ? common.formatDate(step.timestamp, true) : 'Hidden';
            html += `
                <div class="timeline-item pb-4 border-start border-2 ${isLast ? 'border-accent' : 'border-light'} ps-4 position-relative">
                    <span class="position-absolute translate-middle-x ${isLast ? 'bg-accent shadow p-1 border border-accent border-4' : 'bg-light border'} rounded-circle" 
                          style="width:${isLast ? '18px':'14px'}; height:${isLast ? '18px':'14px'}; left:0; top:0; z-index:2;"></span>
                    <div class="small fw-bold ${isLast ? 'text-accent fs-6' : 'text-dark'}">${step.status}</div>
                    <div class="small text-muted mb-1">${timeStr}</div>
                    <div class="extra-small opacity-75 fw-medium">Location: ${step.location}</div>
                </div>
            `;
        });
        container.html(html);
    }
};
