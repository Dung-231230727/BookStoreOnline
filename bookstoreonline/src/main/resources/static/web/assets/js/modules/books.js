/**
 * books.js - Product Management & AI Search
 * Standardized for Full English Backend synchronization
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
        if (!query || !query.trim()) return;
        query = query.trim();

        // Navigate to books page first
        layout.current.isSearching = true;
        if (layout.current.area !== 'Books' || layout.current.view !== 'Index') {
            await layout.render('Books', 'Index');
        }

        // Show loading state in grid
        const grid = $("#books-grid");
        grid.html(`
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-accent mb-3" style="width:2.5rem;height:2.5rem;"></div>
                <p class="text-muted">
                    AI đang tìm kiếm: <strong>"${$('<div>').text(query).html()}"</strong>
                </p>
                <p class="text-muted small">Phân tích ngôn ngữ tự nhiên, lọc theo danh mục và giá...</p>
            </div>
        `);
        $("#product-count").text("...");
        $("#category-title").html(`
            <span class="text-accent" style="font-size:1rem; font-weight:600; display:block; margin-bottom:4px;">
                🔍 Kết quả AI
            </span>
            "${query}"
        `);

        try {
            const res = await api.post('/books/ai-search', { query });
            const results = res.data || [];

            $("#product-count").text(results.length);

            if (!results || results.length === 0) {
                grid.html(`
                    <div class="col-12 text-center py-5">
                        <div style="font-size:3rem;">🔍</div>
                        <h5 class="mt-3 fw-bold">Không tìm thấy kết quả</h5>
                        <p class="text-muted">Không có sách nào khớp với "<strong>${$('<div>').text(query).html()}</strong>"</p>
                        <p class="text-muted small mt-2">Gợi ý: thử từ khóa khác, tên tác giả, hoặc thể loại sách</p>
                        <div class="d-flex gap-2 justify-content-center flex-wrap mt-3">
                            <button class="btn btn-outline-secondary btn-sm rounded-pill" onclick="books.searchAI('sách văn học hay')">📚 Văn học</button>
                            <button class="btn btn-outline-secondary btn-sm rounded-pill" onclick="books.searchAI('kinh doanh khởi nghiệp')">💼 Kinh doanh</button>
                            <button class="btn btn-outline-secondary btn-sm rounded-pill" onclick="books.searchAI('kỹ năng tư duy')">🧠 Kỹ năng</button>
                            <button class="btn btn-outline-secondary btn-sm rounded-pill" onclick="books.loadAll()">Xem tất cả sách</button>
                        </div>
                    </div>
                `);
                return;
            }

            books.renderGrid("#books-grid", results);

            // Update title with summary
            let summaryParts = [];
            if (results.length > 0) summaryParts.push(`${results.length} kết quả`);
            api.showToast(`Tìm thấy ${results.length} sách phù hợp`, "success");

        } catch (error) {
            grid.html(`
                <div class="col-12 text-center py-5 text-danger">
                    <div style="font-size:2.5rem;">⚠️</div>
                    <p class="mt-3">Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại.</p>
                    <button class="btn btn-outline-secondary btn-sm mt-2" onclick="books.loadAll()">Xem tất cả sách</button>
                </div>
            `);
            console.error("AI Search error:", error);
        }
    },

    // 3. Load All Books with Filters
    loadAll: async (filters = {}) => {
        try {
            const page = filters.page || 0;
            const size = filters.size || 12;
            const res = await api.get(`/books?page=${page}&size=${size}`);
            const data = res.data || res;
            
            const itemList = (data && data.content) || (Array.isArray(data) ? data : []);
            books.renderGrid("#books-grid", itemList);
            
            if (data && data.totalPages > 1) {
                console.log("Pagination:", data.totalPages, "pages");
            }
        } catch (error) {
            api.showToast("Failed to load book list", "error");
        }
    },

    // 4. Render Grid Utility
    renderGrid: (selector, itemList) => {
        const container = $(selector);
        if (!container.length) return;

        container.empty();
        $("#product-count").text(itemList ? itemList.length : 0);

        if (!itemList || itemList.length === 0) {
            container.html('<div class="col-12 text-center py-5"><p class="text-muted">No products found.</p></div>');
            return;
        }

        itemList.forEach(book => {
            const priceLabel = api.formatCurrency(book.price);
            const imagePath = book.coverImage ? `assets/images/${book.coverImage}` : 'assets/images/product-item1.jpg';

            container.append(`
                <div class="col-xl-3 col-lg-4 col-sm-6 mb-4">
                    <div class="product-item bg-white p-3 rounded-4 shadow-sm h-100 text-center transition-all hvr-float border-0">
                        <div class="image-holder position-relative mb-4 overflow-hidden rounded-3 bg-light p-2 shadow-sm">
                            <img src="${imagePath}" alt="${book.title}" class="img-fluid" style="height: 250px; object-fit: contain;">
                            <button type="button" class="btn btn-accent w-100 position-absolute bottom-0 start-0 py-2 border-0 opacity-0 transition-all add-to-cart-btn" onclick="cart.add('${book.isbn}', 1)">
                                <i class="icon icon-plus me-2"></i> Add to Cart
                            </button>
                        </div>
                        <div class="product-detail">
                            <h6 class="fw-bold mb-1"><a href="javascript:void(0)" onclick="layout.render('Books', 'Details', '${book.isbn}')" class="text-decoration-none text-dark hvr-accent">${book.title}</a></h6>
                            <div class="product-price fw-bold text-accent">${priceLabel}</div>
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

            $("#book-title").text(b.title);
            $("#book-price").text(api.formatCurrency(b.price));
            $("#book-description").text(b.description || "No description available.");
            $("#book-sku").text(b.isbn);
            $("#book-weight").text(b.weight ? b.weight + "g" : "---");
            $("#book-pages").text(b.pages || "---");
            $("#breadcrumb-category").text(b.categoryName || "Books");

            const imgPath = b.coverImage ? `assets/images/${b.coverImage}` : 'assets/images/product-item1.jpg';
            $("#book-image").attr("src", imgPath);
            $("#btn-add-to-cart").attr("onclick", `cart.add('${b.isbn}', $('#quantity').val())`);

            if (b.categoryId) books.loadRelated(b.categoryId, b.isbn);
        } catch (error) {
            api.showToast("Error loading book details", "error");
        }
    },

    loadRelated: async (categoryId, currentIsbn) => {
        try {
            const res = await api.get('/books');
            const list = res.data || res;
            if (Array.isArray(list)) {
                const related = list.filter(x => x.categoryId === categoryId && x.isbn !== currentIsbn).slice(0, 4);
                books.renderGrid("#related-books-grid", related);
            }
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
                                <img src="${book.coverImage ? 'assets/images/' + book.coverImage : 'assets/images/product-item1.jpg'}" class="rounded shadow-sm" width="50">
                                <div>
                                    <h6 class="fw-bold mb-0">${book.title}</h6>
                                    <small class="text-muted">ISBN: ${book.isbn}</small>
                                </div>
                            </div>
                        </td>
                        <td>${book.categoryName || '---'}</td>
                        <td class="fw-bold text-accent">${api.formatCurrency(book.price)}</td>
                        <td>${book.stockQuantity || 0}</td>
                        <td class="text-end pe-4">
                            <div class="btn-group gap-2">
                                <button class="btn btn-sm btn-light rounded-pill shadow-sm py-1 px-3" onclick="layout.render('Books/Admin', 'Edit', '${book.isbn}')">Edit</button>
                                <button class="btn btn-sm btn-outline-danger rounded-pill shadow-sm" onclick="books.delete('${book.isbn}')"><i class="icon icon-close"></i></button>
                            </div>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Failed to load admin books", "error");
        }
    },

    delete: async (isbn) => {
        if (!confirm("Are you sure you want to delete this book?")) return;
        try {
            await api.delete(`/admin/books/${isbn}`);
            api.showToast("Product deleted successfully");
            books.loadAdminList();
        } catch (e) {
            api.showToast("Failed to delete product", "error");
        }
    },

    // 7. Load Authors for dropdowns
    loadAuthors: async () => {
        try {
            const res = await api.get('/authors');
            const data = res.data || res;
            const sel = $('#authorId');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Select Author --</option>');
            (Array.isArray(data) ? data : []).forEach(a => {
                sel.append(`<option value="${a.id}">${a.fullName || a.name}</option>`);
            });
        } catch (e) { console.warn('loadAuthors failed:', e.message); }
    },

    // 8. Load Publishers for dropdowns
    loadPublishers: async () => {
        try {
            const res = await api.get('/publishers');
            const data = res.data || res;
            const sel = $('#publisherId');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Select Publisher --</option>');
            (Array.isArray(data) ? data : []).forEach(p => {
                sel.append(`<option value="${p.id}">${p.name}</option>`);
            });
        } catch (e) { console.warn('loadPublishers failed:', e.message); }
    },

    // 9. Load Categories into form select
    loadCategoriesIntoForm: async () => {
        try {
            const res = await api.get('/categories');
            const data = res.data || res;
            const sel = $('#categoryId');
            if (!sel.length) return;
            sel.empty().append('<option value="">-- Select Category --</option>');
            
            // Flatten category tree for select dropdown if needed
            const options = [];
            const walk = (nodes, depth = 0) => {
                (Array.isArray(nodes) ? nodes : []).forEach(n => {
                    options.push({ id: n.id || n.categoryId, name: n.name || n.categoryName, depth: depth });
                    if (n.children || n.subCategories) walk(n.children || n.subCategories, depth + 1);
                });
            };
            walk(Array.isArray(data) ? data : []);

            options.forEach(c => {
                const indent = "&nbsp;".repeat(c.depth * 4);
                sel.append(`<option value="${c.id}">${indent}${c.name}</option>`);
            });
        } catch (e) { console.warn('loadCategoriesIntoForm failed:', e.message); }
    },

    // 10. Create new book (standardized payload)
    create: async () => {
        const form = $("#create-book-form");
        if (!form[0].checkValidity()) { form[0].reportValidity(); return; }

        const raw = {};
        form.serializeArray().forEach(item => { raw[item.name] = item.value; });

        const payload = {
            isbn: raw.isbn,
            title: raw.title,
            price: parseFloat(raw.price) || 0,
            pages: raw.pages ? parseInt(raw.pages) : null,
            categoryId: raw.categoryId ? parseInt(raw.categoryId) : null,
            publisherId: raw.publisherId ? parseInt(raw.publisherId) : null,
            description: raw.description || '',
            coverImage: raw.coverImage || '',
            authorIds: raw.authorId ? [parseInt(raw.authorId)] : []
        };

        api.showToast("Saving data...", "info");
        try {
            await api.post('/admin/books', payload);
            api.showToast("Book added successfully!");
            layout.render('Books', 'Admin/Index');
        } catch (e) { api.showToast("Error adding book: " + e.message, "error"); }
    },

    // 11. Update existing book
    update: async (isbn) => {
        const form = $("#edit-book-form");
        if (!form[0].checkValidity()) { form[0].reportValidity(); return; }

        const raw = {};
        form.serializeArray().forEach(item => { raw[item.name] = item.value; });

        const payload = {
            title: raw.title,
            price: parseFloat(raw.price) || 0,
            pages: raw.pages ? parseInt(raw.pages) : null,
            weight: raw.weight ? parseFloat(raw.weight) : null,
            year: raw.year ? parseInt(raw.year) : null,
            categoryId: raw.categoryId ? parseInt(raw.categoryId) : null,
            publisherId: raw.publisherId ? parseInt(raw.publisherId) : null,
            description: raw.description || '',
            coverImage: raw.coverImage || ''
        };

        api.showToast("Updating...", "info");
        try {
            await api.put(`/admin/books/${isbn}`, payload);
            api.showToast("Updated successfully!");
            layout.render('Books', 'Admin/Index');
        } catch (e) { api.showToast("Error updating: " + e.message, "error"); }
    }
};

$(document).on('click', '#btn-create-book', () => books.create());
$(document).on('click', '#btn-update-book', function () {
    const isbn = $(this).data('isbn');
    books.update(isbn);
});
