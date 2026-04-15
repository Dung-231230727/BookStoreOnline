/**
 * categories.js - Category Management Logic
 * API: GET /api/categories → trả cây { maDanhMuc, tenDanhMuc, maDanhMucCha, danhMucCon[] }
 * Renders flat table — children toggle by show/hide rows with data-parent attribute
 */
const categories = {

    _flatList: [],   // full flat list for lookups & selects
    _treeData: [],   // original tree

    // ─── LOAD ───────────────────────────────────────────────────────────────
    loadAdminList: async () => {
        try {
            const res = await api.get('/categories');
            categories._treeData = Array.isArray(res) ? res : (res.data || []);

            // Build flat list (with depth metadata)
            categories._flatList = [];
            categories._walkFlat(categories._treeData, 0, null);

            // Populate dropdowns
            categories._fillParentSelects(null);

            // Render rows
            categories._renderTable();

        } catch (e) {
            console.error('loadAdminList error', e);
            api.showToast('Lỗi tải danh mục', 'error');
        }
    },

    // ─── FLATTEN TREE (recursive) ────────────────────────────────────────────
    _walkFlat: (nodes, depth, parentId) => {
        (nodes || []).forEach(node => {
            categories._flatList.push({
                maDanhMuc:    node.maDanhMuc,
                tenDanhMuc:   node.tenDanhMuc,
                maDanhMucCha: node.maDanhMucCha || null,
                danhMucCon:   node.danhMucCon || [],
                _depth:       depth,
                _parentId:    parentId
            });
            if (node.danhMucCon && node.danhMucCon.length > 0) {
                categories._walkFlat(node.danhMucCon, depth + 1, node.maDanhMuc);
            }
        });
    },

    // ─── RENDER FLAT TABLE ───────────────────────────────────────────────────
    _renderTable: () => {
        const tbody = $('#categories-admin-list');
        if (!tbody.length) return;
        tbody.empty();

        if (categories._flatList.length === 0) {
            tbody.html(`
                <tr><td colspan="4" class="text-center py-5">
                    <div style="color:#ccc; font-size:2rem;">📂</div>
                    <div class="text-muted mt-2">Chưa có danh mục nào.</div>
                </td></tr>`);
            return;
        }

        categories._flatList.forEach(cat => {
            const depth       = cat._depth;
            const hasChildren = cat.danhMucCon.length > 0;
            const childCount  = cat.danhMucCon.length;
            const isRoot      = depth === 0;

            const parentName = cat.maDanhMucCha
                ? (categories._flatList.find(x => x.maDanhMuc === cat.maDanhMucCha)?.tenDanhMuc || `#${cat.maDanhMucCha}`)
                : null;

            const indentPx = depth * 32;

            const levelBadge = isRoot
                ? `<span class="cat-badge-root">Gốc</span>`
                : `<span class="cat-badge-child">Cấp ${depth}</span>`;

            const toggleBtn = hasChildren
                ? `<button class="cat-expand-btn" id="toggle-${cat.maDanhMuc}"
                           onclick="categories._toggle(${cat.maDanhMuc})" title="Mở/đóng danh mục con">
                       <svg width="10" height="10" viewBox="0 0 10 10" fill="currentColor">
                           <path d="M2 3l3 4 3-4H2z"/>
                       </svg>
                   </button>`
                : `<span style="width:20px;display:inline-block;"></span>`;

            const connectorLine = depth > 0
                ? `<span class="cat-connector"></span>`
                : '';

            tbody.append(`
                <tr class="cat-item-row ${isRoot ? 'cat-root-row' : 'cat-child-row'}"
                    data-id="${cat.maDanhMuc}"
                    data-parent="${cat.maDanhMucCha || ''}"
                    data-depth="${depth}"
                    style="${!isRoot ? 'display:none;' : ''}">
                    <td class="cat-cell-id">${cat.maDanhMuc}</td>
                    <td class="cat-cell-name">
                        <div style="display:flex; align-items:center; gap:8px; padding-left:${indentPx}px;">
                            ${connectorLine}
                            ${toggleBtn}
                            <span class="cat-name-text" style="font-weight:${isRoot ? '700' : '500'};">
                                ${cat.tenDanhMuc}
                            </span>
                            ${levelBadge}
                            ${hasChildren ? `<span class="cat-child-count">${childCount} mục con</span>` : ''}
                        </div>
                    </td>
                    <td class="cat-cell-parent">
                        ${parentName
                            ? `<div class="cat-parent-chip">${parentName}</div>`
                            : `<span class="cat-no-parent">—</span>`}
                    </td>
                    <td class="cat-cell-actions">
                        <button class="cat-btn-edit"
                                onclick="categories._editById(${cat.maDanhMuc})">
                            Sửa
                        </button>
                        <button class="cat-btn-delete"
                                onclick="categories._deleteById(${cat.maDanhMuc})">
                            Xóa
                        </button>
                    </td>
                </tr>
            `);
        });

        // Update badge count
        const total = categories._flatList.length;
        const roots = categories._flatList.filter(c => c._depth === 0).length;
        $('#cat-total-badge').text(`${total} mục · ${roots} gốc`);
    },

    // ─── TOGGLE CHILDREN ────────────────────────────────────────────────────
    _toggle: (parentId) => {
        const btn        = $(`#toggle-${parentId}`);
        const isExpanded = btn.hasClass('expanded');
        if (isExpanded) {
            categories._hideDescendants(parentId);
            btn.removeClass('expanded');
        } else {
            $(`[data-parent="${parentId}"]`).show();
            btn.addClass('expanded');
        }
    },

    _hideDescendants: (parentId) => {
        $(`[data-parent="${parentId}"]`).each(function() {
            const childId = $(this).attr('data-id');
            categories._hideDescendants(childId);
            $(`#toggle-${childId}`).removeClass('expanded');
            $(this).hide();
        });
    },

    // ─── BUTTON HANDLERS (ID passed directly as number — most reliable) ──────
    _editById: (id) => {
        console.log('[categories] _editById id=', id);
        const cat = categories._flatList.find(c => c.maDanhMuc === id);
        if (!cat) { console.warn('[categories] not found:', id); return; }
        categories.openEdit(cat.maDanhMuc, cat.tenDanhMuc, cat.maDanhMucCha);
    },

    _deleteById: (id) => {
        console.log('[categories] _deleteById id=', id);
        const cat = categories._flatList.find(c => c.maDanhMuc === id);
        if (!cat) { console.warn('[categories] not found:', id); return; }
        categories.deleteCat(cat.maDanhMuc, cat.tenDanhMuc, cat.danhMucCon.length);
    },

    // ─── FILL PARENT SELECT ──────────────────────────────────────────────────
    _fillParentSelects: (excludeId) => {
        ['#cat-parent-select', '#cat-edit-parent-select'].forEach(sel => {
            const el = $(sel);
            if (!el.length) return;
            const prev = el.val();
            el.empty().append('<option value="">— Không (là danh mục gốc) —</option>');
            categories._flatList.forEach(cat => {
                if (excludeId && (cat.maDanhMuc === excludeId || cat._parentId === excludeId)) return;
                const prefix = '\u00a0\u00a0\u00a0\u00a0'.repeat(cat._depth);
                el.append(`<option value="${cat.maDanhMuc}">${prefix}${cat._depth > 0 ? '└ ' : ''}${cat.tenDanhMuc}</option>`);
            });
            if (prev) el.val(prev);
        });
    },

    // ─── CREATE ─────────────────────────────────────────────────────────────
    create: async () => {
        const tenDanhMuc   = $('#cat-name').val().trim();
        const maDanhMucCha = $('#cat-parent-select').val() || null;

        if (!tenDanhMuc) { api.showToast('Vui lòng nhập tên danh mục', 'warning'); return; }

        try {
            await api.post('/admin/categories', {
                tenDanhMuc,
                maDanhMucCha: maDanhMucCha ? parseInt(maDanhMucCha) : null
            });
            api.showToast('Thêm danh mục thành công!');
            $('#cat-name').val('');
            $('#cat-parent-select').val('');
            $('#cat-create-section').slideUp();
            categories.loadAdminList();
        } catch (e) {
            api.showToast('Lỗi khi thêm: ' + e.message, 'error');
        }
    },

    // ─── EDIT ────────────────────────────────────────────────────────────────
    openEdit: (id, tenDanhMuc, maDanhMucCha) => {
        $('#cat-edit-id').val(id);
        $('#cat-edit-name').val(tenDanhMuc);
        categories._fillParentSelects(id);
        $('#cat-edit-parent-select').val(maDanhMucCha || '');
        $('#cat-create-section').slideUp();
        $('#cat-edit-section').slideDown();
        document.getElementById('cat-edit-section')?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    },

    update: async () => {
        const id           = $('#cat-edit-id').val();
        const tenDanhMuc   = $('#cat-edit-name').val().trim();
        const maDanhMucCha = $('#cat-edit-parent-select').val() || null;

        if (!id || !tenDanhMuc) { api.showToast('Thiếu thông tin', 'warning'); return; }

        try {
            await api.put(`/admin/categories/${id}`, {
                tenDanhMuc,
                maDanhMucCha: maDanhMucCha ? parseInt(maDanhMucCha) : null
            });
            api.showToast('Cập nhật thành công!');
            $('#cat-edit-section').slideUp();
            categories.loadAdminList();
        } catch (e) {
            api.showToast('Lỗi cập nhật: ' + e.message, 'error');
        }
    },

    // ─── DELETE ──────────────────────────────────────────────────────────────
    // Dùng custom confirm modal (window.confirm có thể bị block trong SPA context)
    deleteCat: (id, tenDanhMuc, childCount) => {
        const name = tenDanhMuc || `Danh mục #${id}`;
        childCount = childCount || 0;

        categories._showConfirm(
            `Xóa danh mục <b>"${name}"</b>?`,
            childCount > 0
                ? `⚠️ Danh mục này có ${childCount} mục con — tất cả sẽ bị xóa theo.`
                : 'Hành động này không thể hoàn tác.',
            async () => {
                try {
                    await api.delete(`/admin/categories/${id}`);
                    api.showToast(`Đã xóa "${name}" thành công!`);
                    categories.loadAdminList();
                } catch (e) {
                    const errMsg = (e?.message || '').toLowerCase();
                    const isFK = errMsg.includes('sách') || errMsg.includes('constraint')
                              || errMsg.includes('foreign') || errMsg.includes('integrity')
                              || errMsg.includes('tham chiếu') || errMsg.includes('reference');
                    api.showToast(
                        isFK
                            ? `Không thể xóa — "${name}" hoặc mục con đang có sách liên kết. Hãy chuyển sách trước.`
                            : `Xóa thất bại: ${e?.message || 'Lỗi không xác định'}`,
                        'error'
                    );
                }
            }
        );
    },

    // ─── CUSTOM CONFIRM MODAL ────────────────────────────────────────────────
    // window.confirm() bị block trong nhiều SPA context → dùng custom overlay
    _showConfirm: (title, subtitle, onConfirm) => {
        $('#cat-confirm-overlay').remove();

        const overlay = $(`
            <div id="cat-confirm-overlay" style="
                position:fixed; top:0; left:0; right:0; bottom:0; z-index:99999;
                background:rgba(0,0,0,0.5);
                display:flex; align-items:center; justify-content:center;">
                <div style="
                    background:#fff; border-radius:16px; padding:32px;
                    max-width:400px; width:90%; box-shadow:0 20px 60px rgba(0,0,0,0.3);
                    text-align:center;">
                    <div style="font-size:2rem; margin-bottom:12px;">🗑️</div>
                    <div style="font-weight:700; color:#1a1a1a; font-size:1rem; margin-bottom:8px;">${title}</div>
                    <div style="color:#888; font-size:0.85rem; margin-bottom:28px;">${subtitle}</div>
                    <div style="display:flex; gap:12px;">
                        <button id="cat-confirm-cancel" style="
                            flex:1; padding:12px; border:1.5px solid #ddd; border-radius:10px;
                            background:#fff; color:#555; font-weight:600; cursor:pointer; font-size:0.875rem;">
                            Hủy
                        </button>
                        <button id="cat-confirm-ok" style="
                            flex:1; padding:12px; border:none; border-radius:10px;
                            background:#ef4444; color:#fff; font-weight:700; cursor:pointer; font-size:0.875rem;">
                            Xóa
                        </button>
                    </div>
                </div>
            </div>
        `);

        $('body').append(overlay);

        $('#cat-confirm-cancel').on('click', () => overlay.remove());
        overlay.on('click', function(e) { if (e.target === this) overlay.remove(); });
        $('#cat-confirm-ok').on('click', async () => {
            overlay.remove();
            await onConfirm();
        });
    }
};
