/**
 * books.js - Product Management & AI Search (Person 2)
 */
let currentBookIsbn = null;

const books = {
    // 1. Fetch and display featured books on homepage
    loadFeatured: async () => {
        try {
            const res = await api.get('/books');
            const itemList = res.data || res;
            if (Array.isArray(itemList)) {
                books.renderGrid("#featured-books-container", itemList.slice(0, 8));
            }
        } catch (error) {
            console.error("Failed to load featured books", error);
        }
    },

    // 2. AI Search Integration
    searchAI: async (query) => {
        if (!query) return;
        api.showToast("AI đang tìm kiếm...", "info");
        try {
            // Chuyển view trước khi gọi API (đánh dấu là đang search để tránh loadAll ghi đè)
            layout.current.isSearching = true;
            if (layout.current.view !== "Books/Index") {
                await layout.render('Books', 'Index');
            }

            const res = await api.post('/books/ai-search', { query: query });
            const results = res.data;
            console.log("AI Search Results:", results);

            $("#category-title").text(`Kết quả AI cho: "${query}"`);
            books.renderGrid("#books-grid", results);

            if (!results || results.length === 0) {
                api.showToast("Rất tiếc, AI không tìm thấy sách phù hợp", "warning");
            }
        } catch (error) {
            api.showToast("Lỗi tìm kiếm AI", "error");
        }
    },

    // 3. Load All Books with Filters
    loadAll: async (filters = {}) => {
        try {
            const res = await api.get('/books');
            const itemList = res.data || res;
            books.renderGrid("#books-grid", itemList);
        } catch (error) {
            api.showToast("Lỗi tải danh sách sách", "error");
        }
    },

    // 4. Render Grid Utility
    renderGrid: (selector, itemList) => {
        const container = $(selector);
        console.log(`[renderGrid] Target: ${selector}, Matches: ${container.length}, Items: ${itemList ? itemList.length : 0}`);

        if (!container.length) {
            console.warn(`[renderGrid] Container ${selector} not found in DOM!`);
            return;
        }

        container.empty();
        $("#product-count").text(itemList ? itemList.length : 0);

        if (!itemList || itemList.length === 0) {
            container.html('<div class="col-12 text-center py-5"><p class="text-muted">Không tìm thấy sản phẩm nào.</p></div>');
            return;
        }

        itemList.forEach(book => {
            const price = api.formatCurrency(book.giaNiemYet);
            const image = book.anhBia ? `assets/images/${book.anhBia}` : 'assets/images/product-item1.jpg';

            container.append(`
                <div class="col-xl-3 col-lg-4 col-sm-6 mb-4">
                    <div class="product-item bg-white p-3 rounded-4 shadow-sm h-100 text-center transition-all hvr-float border-0">
                        <div class="image-holder position-relative mb-4 overflow-hidden rounded-3 bg-light p-2 shadow-sm">
                            <img src="${image}" alt="${book.tenSach}" class="img-fluid" style="height: 250px; object-fit: contain;">
                            <button type="button" class="btn btn-accent w-100 position-absolute bottom-0 start-0 py-2 border-0 opacity-0 transition-all add-to-cart-btn" onclick="cart.add('${book.isbn}', 1)">
                                <i class="icon icon-plus me-2"></i> Thêm vào giỏ
                            </button>
                        </div>
                        <div class="product-detail">
                            <h6 class="fw-bold mb-1"><a href="javascript:void(0)" onclick="layout.render('Books', 'Details', '${book.isbn}')" class="text-decoration-none text-dark hvr-accent">${book.tenSach}</a></h6>
                            <div class="product-price fw-bold text-accent">${price}</div>
                        </div>
                    </div>
                </div>
            `);
        });
    },

    // 5. Book Details & Related
    loadDetail: async (isbn) => {
        currentBookIsbn = isbn;
        try {
            const res = await api.get(`/books/${isbn}`);
            const b = res.data || res;

            $("#book-title").text(b.tenSach);
            $("#book-price").text(api.formatCurrency(b.giaNiemYet));
            $("#book-description").text(b.moTaNguNghia || b.moTa || "Chưa có mô tả.");
            $("#book-sku").text(b.isbn);
            $("#book-weight").text(b.trongLuong + "g" || "---");
            $("#book-pages").text(b.soTrang || "---");
            $("#breadcrumb-category").text(b.danhMuc ? b.danhMuc.tenDanhMuc : "Sách");

            const imgPath = b.anhBia ? `assets/images/${b.anhBia}` : 'assets/images/product-item1.jpg';
            $("#book-image").attr("src", imgPath);
            $("#btn-add-to-cart").attr("onclick", `cart.add('${b.isbn}', $('#quantity').val())`);

            if (b.danhMuc) books.loadRelated(b.danhMuc.maDanhMuc, b.isbn);
        } catch (error) {
            api.showToast("Lỗi tải chi tiết sách", "error");
        }
    },

    loadRelated: async (catId, currentIsbn) => {
        try {
            const res = await api.get('/books');
            const list = res.data || res;
            const related = list.filter(x => x.danhMuc && x.danhMuc.maDanhMuc === catId && x.isbn !== currentIsbn).slice(0, 4);
            books.renderGrid("#related-books-grid", related);
        } catch (e) { }
    },

    // 6. Admin Management
    loadAdminList: async () => {
        try {
            const res = await api.get('/books');
            const data = res.data || res;
            const tbody = $("#books-admin-list");
            if (!tbody.length) return;
            tbody.empty();

            data.forEach(book => {
                tbody.append(`
                    <tr>
                        <td class="ps-4 py-4">
                            <div class="d-flex align-items-center gap-3">
                                <img src="${book.anhBia ? 'assets/images/' + book.anhBia : 'assets/images/product-item1.jpg'}" class="rounded shadow-sm" width="50">
                                <div>
                                    <h6 class="fw-bold mb-0">${book.tenSach}</h6>
                                    <small class="text-muted">ISBN: ${book.isbn}</small>
                                </div>
                            </div>
                        </td>
                        <td>${book.danhMuc ? book.danhMuc.tenDanhMuc : '---'}</td>
                        <td class="fw-bold text-accent">${api.formatCurrency(book.giaNiemYet)}</td>
                        <td>${book.soLuongHienTai || 0}</td>
                        <td class="text-end pe-4">
                            <div class="btn-group gap-2">
                                <button class="btn btn-sm btn-light rounded-pill shadow-sm py-1 px-3" onclick="layout.render('Books/Admin', 'Edit', '${book.isbn}')">Sửa</button>
                                <button class="btn btn-sm btn-outline-danger rounded-pill shadow-sm" onclick="books.delete('${book.isbn}')"><i class="icon icon-close"></i></button>
                            </div>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Lỗi tải danh sách quản trị", "error");
        }
    },

    delete: async (isbn) => {
        if (!confirm("Bạn có chắc chắn muốn xóa sách này (Xóa mềm)?")) return;
        try {
            await api.delete(`/admin/books/${isbn}`);
            api.showToast("Đã xóa sản phẩm thành công");
            books.loadAdminList();
        } catch (e) {
            api.showToast("Không thể xóa sản phẩm", "error");
        }
    },

    // 7. Load Authors for dropdowns (GET /api/authors)
    loadAuthors: async () => {
        try {
            const res = await api.get('/authors');
            const data = res.data || res;
            const sel = $('#maTacGia');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Chọn tác giả --</option>');
            (Array.isArray(data) ? data : []).forEach(a => {
                sel.append(`<option value="${a.maTacGia}">${a.tenTacGia}</option>`);
            });
        } catch (e) { console.warn('loadAuthors failed:', e.message); }
    },

    // 8. Load Publishers for dropdowns (GET /api/publishers)
    loadPublishers: async () => {
        try {
            const res = await api.get('/publishers');
            const data = res.data || res;
            const sel = $('#maNxb');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Chọn NXB --</option>');
            (Array.isArray(data) ? data : []).forEach(p => {
                sel.append(`<option value="${p.maNxb}">${p.tenNxb}</option>`);
            });
        } catch (e) { console.warn('loadPublishers failed:', e.message); }
    },

    // 9. Load Categories into form select
    loadCategoriesIntoForm: async () => {
        try {
            const res = await api.get('/categories');
            const data = res.data || res;
            const sel = $('#maDanhMuc');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Chọn danh mục --</option>');
            (Array.isArray(data) ? data : []).forEach(c => {
                sel.append(`<option value="${c.maDanhMuc}">${c.tenDanhMuc}</option>`);
            });
        } catch (e) { console.warn('loadCategoriesIntoForm failed:', e.message); }
    },

    // 10. Create new book (POST /api/admin/books)
    create: async () => {
        const form = $("#create-book-form");
        if (!form[0].checkValidity()) { form[0].reportValidity(); return; }

        const raw = {};
        form.serializeArray().forEach(item => { raw[item.name] = item.value; });

        // SachCreateRequest expects flat Integer fields, not nested objects
        const payload = {
            isbn: raw.isbn,
            tenSach: raw.tenSach,
            giaNiemYet: parseFloat(raw.giaNiemYet) || 0,
            soTrang: raw.soTrang ? parseInt(raw.soTrang) : null,
            maDanhMuc: raw.maDanhMuc ? parseInt(raw.maDanhMuc) : null,
            maNxb: raw.maNxb ? parseInt(raw.maNxb) : null,
            moTaNguNghia: raw.moTa || '',
            anhBia: raw.anhBia || '',
            tacGiaIds: raw.maTacGia ? [parseInt(raw.maTacGia)] : []
        };

        api.showToast("Đang lưu dữ liệu...", "info");
        try {
            await api.post('/admin/books', payload);
            api.showToast("Thêm sách thành công!");
            layout.render('Books', 'Admin/Index');
        } catch (e) { api.showToast("Lỗi khi thêm sách: " + e.message, "error"); }
    },

    // 11. Update existing book (PUT /api/admin/books/{isbn})
    update: async (isbn) => {
        const form = $("#edit-book-form");
        if (!form[0].checkValidity()) { form[0].reportValidity(); return; }

        const raw = {};
        form.serializeArray().forEach(item => { raw[item.name] = item.value; });

        const payload = {
            tenSach: raw.tenSach,
            giaNiemYet: parseFloat(raw.giaNiemYet) || 0,
            soTrang: raw.soTrang ? parseInt(raw.soTrang) : null,
            trongLuong: raw.trongLuong ? parseFloat(raw.trongLuong) : null,
            namXuatBan: raw.namXuatBan ? parseInt(raw.namXuatBan) : null,
            maDanhMuc: raw.maDanhMuc ? parseInt(raw.maDanhMuc) : null,
            maNxb: raw.maNxb ? parseInt(raw.maNxb) : null,
            moTaNguNghia: raw.moTa || '',
            anhBia: raw.anhBia || ''
        };

        api.showToast("Đang cập nhật...", "info");
        try {
            await api.put(`/admin/books/${isbn}`, payload);
            api.showToast("Cập nhật thành công!");
            layout.render('Books', 'Admin/Index');
        } catch (e) { api.showToast("Lỗi khi cập nhật: " + e.message, "error"); }
    },

    // 12. Legacy save() — kept for backward compat
    save: async () => books.create()
};

$(document).on('click', '#btn-save-book', () => books.create());
$(document).on('click', '#btn-update-book', function () {
    const isbn = $(this).data('isbn');
    books.update(isbn);
});
