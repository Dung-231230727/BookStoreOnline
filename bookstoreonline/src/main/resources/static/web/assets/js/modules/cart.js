/**
 * cart.js - Shopping Cart Logic
 * Aligned with CartController.java backend API:
 *   GET    /api/cart/{username}
 *   POST   /api/cart/add?username=X&isbn=Y&soLuong=Z
 *   PUT    /api/cart/update?id=N&soLuong=Z        ← uses item.id (Long)
 *   DELETE /api/cart/remove/{id}                  ← uses item.id (Long)
 *   DELETE /api/cart/clear/{username}
 */
const cart = {

    add: async (isbn, quantity = 1) => {
        const user = api.getUser();
        if (!user) {
            api.showToast("Vui lòng đăng nhập để thêm vào giỏ hàng", "warning");
            layout.render('Auth', 'Login');
            return;
        }
        try {
            await api.post(`/cart/add?username=${user.username}&isbn=${isbn}&soLuong=${quantity}`);
            api.showToast("Đã thêm vào giỏ hàng!");
            cart.updateCounter();
        } catch (error) {
            api.showToast("Lỗi khi thêm vào giỏ hàng", "error");
        }
    },

    load: async () => {
        const user = api.getUser();
        if (!user) { cart.render([]); return; }
        try {
            const result = await api.get(`/cart/${user.username}`);
            const items = result.data || [];
            cart.render(items);
        } catch (error) {
            console.error("Cart load failed", error);
            cart.render([]);
        }
    },

    updateCounter: async () => {
        const user = api.getUser();
        if (!user) { $("#cart-count").text(0); return; }
        try {
            const result = await api.get(`/cart/${user.username}`);
            const items = result.data || [];
            $("#cart-count").text(items.length);
        } catch (error) {
            $("#cart-count").text(0);
        }
    },

    render: (items) => {
        const body = $("#cart-items-body");
        if (!body.length) return;
        body.empty();
        let subtotal = 0;

        if (!items || items.length === 0) {
            body.html(`<tr><td colspan="3" class="text-center py-5 text-muted">Giỏ hàng của bạn đang trống. <a href="javascript:void(0)" onclick="layout.render('Books', 'Index')" class="text-accent">Mua sắm ngay</a></td></tr>`);
            $("#cart-subtotal").text("0đ");
            $("#cart-final-total").text("0đ");
            $("#cart-item-count").text(0);
            return;
        }

        items.forEach(item => {
            // Use thanhTien from backend if available, else calculate
            const itemTotal = item.thanhTien || (item.giaNiemYet * item.soLuong);
            subtotal += parseFloat(itemTotal);

            body.append(`
                <tr class="border-bottom">
                    <td class="ps-4 py-4">
                        <div class="d-flex align-items-center gap-3">
                            <img src="${item.anhBia ? 'assets/images/' + item.anhBia : 'assets/images/product-item1.jpg'}"
                                 class="rounded-3 shadow-sm" width="80"
                                 onerror="this.src='assets/images/product-item1.jpg'">
                            <div>
                                <h6 class="fw-bold mb-1">${item.tenSach}</h6>
                                <p class="small text-muted mb-0">ISBN: ${item.isbn}</p>
                                <button onclick="cart.remove(${item.id})" class="btn btn-link link-danger p-0 small text-decoration-none mt-2">
                                    <i class="icon icon-close me-1"></i> Xóa
                                </button>
                            </div>
                        </div>
                    </td>
                    <td class="text-center">
                        <div class="quantity-input d-inline-flex align-items-center border rounded-pill bg-light mx-auto">
                            <button onclick="cart.update(${item.id}, ${item.soLuong - 1})" class="btn btn-sm link-dark border-0 p-2 shadow-none"><i class="icon icon-minus"></i></button>
                            <input type="number" class="form-control border-0 bg-transparent text-center fw-bold shadow-none p-0" value="${item.soLuong}" style="width: 40px" readonly>
                            <button onclick="cart.update(${item.id}, ${item.soLuong + 1})" class="btn btn-sm link-dark border-0 p-2 shadow-none"><i class="icon icon-plus"></i></button>
                        </div>
                    </td>
                    <td class="text-end pe-4 fw-bold text-accent fs-5">${api.formatCurrency(itemTotal)}</td>
                </tr>
            `);
        });

        const shippingFee = 30000;
        $("#cart-item-count").text(items.length);
        $("#cart-subtotal").text(api.formatCurrency(subtotal));
        $("#cart-shipping").text(api.formatCurrency(shippingFee));
        $("#cart-final-total").text(api.formatCurrency(subtotal + shippingFee));
    },

    // PUT /api/cart/update?id=N&soLuong=Z  (Backend uses Long id, not isbn)
    update: async (id, quantity) => {
        if (quantity < 1) {
            cart.remove(id);
            return;
        }
        try {
            await api.put(`/cart/update?id=${id}&soLuong=${quantity}`);
            cart.load();
            cart.updateCounter();
        } catch (e) {
            api.showToast("Lỗi khi cập nhật số lượng", "error");
        }
    },

    // DELETE /api/cart/remove/{id}  (Backend uses Long id, not isbn)
    remove: async (id) => {
        if (!confirm("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")) return;
        try {
            await api.delete(`/cart/remove/${id}`);
            api.showToast("Đã xóa khỏi giỏ hàng");
            cart.load();
            cart.updateCounter();
        } catch (e) {
            api.showToast("Lỗi khi xóa sản phẩm", "error");
        }
    },

    // DELETE /api/cart/clear/{username}
    clearAll: async () => {
        if (!confirm("Bạn có chắc muốn xóa toàn bộ giỏ hàng?")) return;
        const user = api.getUser();
        if (!user) return;
        try {
            await api.delete(`/cart/clear/${user.username}`);
            api.showToast("Đã xóa toàn bộ giỏ hàng");
            cart.render([]);
            cart.updateCounter();
        } catch (e) {
            api.showToast("Lỗi khi xóa giỏ hàng", "error");
        }
    }
};
