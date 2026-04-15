# BookStore Online - Detailed Fix Instructions

## 🔧 How to Apply These Fixes

Each fix includes:
- **File path** - Where to make changes
- **Current code** - What to find
- **Fixed code** - What to replace with
- **Why** - Explanation of the issue

---

## CRITICAL FIX #1: Payment Endpoints (HIGHEST PRIORITY)

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/modules/payments.js`

#### Fix 1.1 - Change endpoint from Vietnamese to English (Line 18)
```javascript
// CURRENT (WRONG):
const res = await api.get(`/thanhtoan`, { search, status, method });

// FIX:
const res = await api.get(`/payments`, { search, status, method });
```

#### Fix 1.2 - Update field names throughout payment module

**Line ~40-60** - loadAdminPayments() method:
```javascript
// CURRENT (WRONG):
paymentsList.forEach(payment => {
    const statusBadge = payments.getStatusBadge(payment.trangThai);
    tbody.append(`
        <tr onclick="layout.render('Payments/Admin', 'Details')">
            <td class="ps-4 fw-bold">#${payment.maDonHang}</td>
            <td>${payment.tenKhachHang}</td>
            <td>${payment.phuongThucThanhToan}</td>
            <td class="text-end text-accent fw-bold">${formatCurrency(payment.soTien)}</td>
            <td>${statusBadge}</td>
            <td>${new Date(payment.thoiGianThanhToan).toLocaleString('vi-VN')}</td>

// FIX:
paymentsList.forEach(payment => {
    const statusBadge = payments.getStatusBadge(payment.statusCode);
    tbody.append(`
        <tr onclick="layout.render('Payments/Admin', 'Details')">
            <td class="ps-4 fw-bold">#${payment.orderId}</td>
            <td>${payment.customerName}</td>
            <td>${payment.paymentMethod}</td>
            <td class="text-end text-accent fw-bold">${formatCurrency(payment.amount)}</td>
            <td>${statusBadge}</td>
            <td>${new Date(payment.paymentDate).toLocaleString('vi-VN')}</td>
```

#### Fix 1.3 - Update field references in getStatusBadge() (Line ~150+)
```javascript
// CURRENT (WRONG):
const badges = {
    'PENDING': '<span class="badge bg-warning text-dark">Chờ xác nhận</span>',
    'SUCCESS': '<span class="badge bg-success">Thành công</span>',
    'FAILED': '<span class="badge bg-danger">Thất bại</span>'
};

// FIX:
const badges = {
    'PENDING': '<span class="badge bg-warning text-dark">Pending</span>',
    'SUCCESS': '<span class="badge bg-success">Success</span>',
    'FAILED': '<span class="badge bg-danger">Failed</span>'
};
```

#### Field Name Mapping Reference:
| Vietnamese | English |
|---|---|
| trangThai | statusCode |
| maDonHang | orderId |
| tenKhachHang | customerName |
| soTien | amount |
| phuongThucThanhToan | paymentMethod |
| thoiGianThanhToan | paymentDate |
| maGiaoDich | transactionReference |
| maNganHang | bankCode |
| diaChiIP | ipAddress |
| moTa | description |
| tieuPhu | subtotal |
| phiVanChuyen | shippingFee |
| giamGia | discount |

---

## CRITICAL FIX #2: Audit Log Field Names

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/modules/auditlog.js`

#### Fix 2.1 - Update loadLogs() method (Lines 20-70)
```javascript
// CURRENT (WRONG):
logs.forEach(log => {
    const actionBadge = auditlog.getActionBadge(log.hanhDong);
    tbody.append(`
        <tr onclick="auditlog.viewDetail('${log.maAudit}')">
            <td>${formatDateTime(log.thoiGian)}</td>
            <td><strong>${log.tenNguoiDung}</strong></td>
            <td>${actionBadge}</td>
            <td>${log.loaiDoiTuong}</td>
            <td title="${log.moTa}" class="text-truncate">${log.moTa || '--'}</td>
            <td><small class="text-muted">${log.diaChiIP || '--'}</small></td>

// FIX:
logs.forEach(log => {
    const actionBadge = auditlog.getActionBadge(log.action);
    tbody.append(`
        <tr onclick="auditlog.viewDetail('${log.id}')">
            <td>${formatDateTime(log.timestamp)}</td>
            <td><strong>${log.username}</strong></td>
            <td>${actionBadge}</td>
            <td>${log.targetType}</td>
            <td title="${log.details}" class="text-truncate">${log.details || '--'}</td>
            <td><small class="text-muted">${log.ipAddress || '--'}</small></td>
```

#### Fix 2.2 - Update viewDetail() method (Lines 80-95)
```javascript
// CURRENT (WRONG):
const detailHTML = `
    <div class="alert alert-light border-0 bg-light rounded-3 p-3">
        <small class="d-block mb-2"><strong>Thời gian:</strong> ${formatDateTime(log.thoiGian)}</small>
        <small class="d-block mb-2"><strong>Người dùng:</strong> ${log.tenNguoiDung} (${log.username})</small>
        <small class="d-block mb-2"><strong>Hành động:</strong> ${log.hanhDong}</small>
        <small class="d-block mb-2"><strong>Đối tượng:</strong> ${log.loaiDoiTuong}</small>
        <small class="d-block mb-2"><strong>Mô tả:</strong> ${log.moTa}</small>
        <small class="d-block"><strong>IP Address:</strong> ${log.diaChiIP}</small>

// FIX:
const detailHTML = `
    <div class="alert alert-light border-0 bg-light rounded-3 p-3">
        <small class="d-block mb-2"><strong>Timestamp:</strong> ${formatDateTime(log.timestamp)}</small>
        <small class="d-block mb-2"><strong>User:</strong> ${log.username}</small>
        <small class="d-block mb-2"><strong>Action:</strong> ${log.action}</small>
        <small class="d-block mb-2"><strong>Target:</strong> ${log.targetType}</small>
        <small class="d-block mb-2"><strong>Details:</strong> ${log.details}</small>
        <small class="d-block"><strong>IP Address:</strong> ${log.ipAddress}</small>
```

#### Fix 2.3 - Update updateStatistics() method (Lines 105-115)
```javascript
// CURRENT (WRONG):
$("#audit-total-count").text(stats.tongHoatDong || 0);
$("#audit-today-count").text(stats.homNay || 0);
$("#audit-active-users").text(stats.nguoiDungHoatDong || 0);
$("#audit-changes-count").text(stats.chiTinhSuaDoi || 0);

// FIX:
$("#audit-total-count").text(stats.totalActivities || 0);
$("#audit-today-count").text(stats.todayCount || 0);
$("#audit-active-users").text(stats.activeUsers || 0);
$("#audit-changes-count").text(stats.mutations || 0);
```

#### Field Name Mapping:
| Vietnamese | English |
|---|---|
| hanhDong | action |
| moTa | details |
| thoiGian | timestamp |
| tenNguoiDung | username |
| diaChiIP | ipAddress |
| maAudit | id |
| loaiDoiTuong | targetType |
| tongHoatDong | totalActivities |
| homNay | todayCount |
| nguoiDungHoatDong | activeUsers |
| chiTinhSuaDoi | mutations |

---

## CRITICAL FIX #3: Dashboard Book Rankings

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/admin/dashboard.js`

#### Fix 3.1 - Update loadRanking() method (Lines 45-60)
```javascript
// CURRENT (POSSIBLY WRONG):
data.forEach((item, i) => {
    tbody.innerHTML += `
        <tr>
            <td class="fw-bold">${item.title || item.isbn}</td>
            <td class="text-end pe-3 fw-bold text-accent">${item.totalSold || 0} books</td>
        </tr>
    `;
});

// LIKELY FIX (depends on BE BookRankingDTO):
data.forEach((item, i) => {
    tbody.innerHTML += `
        <tr>
            <td class="fw-bold">${item.title || item.bookTitle || item.isbn}</td>
            <td class="text-end pe-3 fw-bold text-accent">${item.totalSold || item.salesCount || 0} books</td>
        </tr>
    `;
});
```

**ACTION**: You need to check BookRankingDTO in BE to confirm exact field names. Check these files:
- `com/bookstore/dto/BookRankingDTO.java`
- `com/bookstore/service/AdminDashboardService.java` (getBookRanking method)

---

## CRITICAL FIX #4: Password Change Request Fields

### Location: `bookstoreonline/src/main/resources/static/web/Auth/ChangePassword.html`

#### Fix 4.1 - Update form submission (Lines 350-380)
```javascript
// CURRENT (POSSIBLY WRONG):
api.put("/auth/change-password", {
    oldPassword:     currentPassword,
    newPassword:     newPassword,
    confirmPassword: confirmPassword
})

// LIKELY FIX (check BE ChangePasswordRequest):
api.put("/auth/change-password", {
    currentPassword: currentPassword,  // or oldPassword?
    newPassword:     newPassword
    // Remove confirmPassword - it's FE validation only
})
```

**ACTION**: Check these BE files first:
- `com/bookstore/dto/ChangePasswordRequest.java`
- `com/bookstore/controller/AuthController.changePassword()`
- `com/bookstore/service/AuthService.changePassword()`

**Common patterns in Spring Boot**:
- Usually: `currentPassword` or `oldPassword`
- Usually: `newPassword`
- Usually: NO `confirmPassword` (only validated on FE)

---

## HIGH PRIORITY FIX #5: Standardize API Response Handling

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/common.js`

#### Fix 5.1 - Create standardized response parser (Add after line 150)
```javascript
// ADD THIS NEW FUNCTION:
api.parseResponse = (response) => {
    // Handle ApiResponse<T> format { status, message, data }
    if (response && typeof response === 'object') {
        if (Array.isArray(response.data)) {
            return response.data; // Return data array
        } else if (response.data !== null && response.data !== undefined) {
            return response.data; // Return single object
        }
    }
    // Fallback for direct array responses
    return Array.isArray(response) ? response : [];
};
```

#### Fix 5.2 - Update modules to use standardized parsing

In `modules/books.js`:
```javascript
// BEFORE:
const itemList = res.data || res;
if (Array.isArray(itemList)) {
    books.renderGrid("#featured-books-container", itemList.slice(0, 8));
}

// AFTER:
const itemList = api.parseResponse(res);
books.renderGrid("#featured-books-container", itemList.slice(0, 8));
```

In `modules/categories.js`:
```javascript
// BEFORE:
categories._treeData = Array.isArray(res) ? res : (res.data || []);

// AFTER:
categories._treeData = api.parseResponse(res);
```

---

## HIGH PRIORITY FIX #6: Review Field Names

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/modules/review.js`

#### Fix 6.1 - Update renderSection() method (Lines 23-40)
```javascript
// CURRENT (WRONG):
data.forEach(r => {
    const stars = '★'.repeat(r.diemDg) + '☆'.repeat(5 - r.diemDg);
    container.append(`
        <div class="review-item mb-4 pb-4 border-bottom">
            <div class="d-flex justify-content-between mb-2">
                <span class="fw-bold text-dark">${r.tenKhachHang || 'Khách hàng'}</span>
                <span class="text-warning">${stars}</span>
            </div>
            <p class="text-muted mb-1">${r.nhanXet}</p>
            <small class="text-muted-50">${new Date(r.ngayDg).toLocaleDateString('vi-VN')}</small>

// FIX:
data.forEach(r => {
    const stars = '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating);
    container.append(`
        <div class="review-item mb-4 pb-4 border-bottom">
            <div class="d-flex justify-content-between mb-2">
                <span class="fw-bold text-dark">${r.username || 'Anonymous'}</span>
                <span class="text-warning">${stars}</span>
            </div>
            <p class="text-muted mb-1">${r.comment}</p>
            <small class="text-muted-50">${new Date(r.createdAt).toLocaleDateString('vi-VN')}</small>
```

#### Fix 6.2 - Update submit() method (Lines 50-65)
```javascript
// CURRENT (WRONG):
await api.post(`/reviews/submit?username=${user.username}&isbn=${isbn}&diem=${diem}&nhanXet=${encodeURIComponent(noidung)}`);

// FIX:
await api.post(`/reviews/submit`, {
    username: user.username,
    isbn: isbn,
    rating: parseInt(diem),
    comment: noidung
});
```

#### Fix 6.3 - Update renderAdminTable() method (Lines 95-110)
```javascript
// CURRENT (WRONG):
data.forEach(r => {
    tbody.append(`
        <tr>
            <td class="ps-4">
                <div class="fw-bold">${r.tenKhachHang || 'Khách hàng'}</div>
                <div class="small text-muted">ISBN: ${r.isbn || '---'}</div>
            </td>
            <td><span class="text-warning">${'★'.repeat(r.diemDg || 0)}</span></td>
            <td style="max-width: 300px;" class="text-truncate">${r.nhanXet}</td>
            <td>${new Date(r.ngayDg).toLocaleDateString('vi-VN')}</td>
            <td class="text-end pe-4">
                <button onclick="review.delete(${r.maDg})" class="btn btn-sm btn-outline-danger border-0">Xóa</button>

// FIX:
data.forEach(r => {
    tbody.append(`
        <tr>
            <td class="ps-4">
                <div class="fw-bold">${r.username || 'Anonymous'}</div>
                <div class="small text-muted">ISBN: ${r.isbn || '---'}</div>
            </td>
            <td><span class="text-warning">${'★'.repeat(r.rating || 0)}</span></td>
            <td style="max-width: 300px;" class="text-truncate">${r.comment}</td>
            <td>${new Date(r.createdAt).toLocaleDateString('vi-VN')}</td>
            <td class="text-end pe-4">
                <button onclick="review.delete(${r.id})" class="btn btn-sm btn-outline-danger border-0">Delete</button>
```

#### Field Name Mapping:
| Vietnamese | English |
|---|---|
| tenKhachHang | username |
| diemDg | rating |
| nhanXet | comment |
| ngayDg | createdAt |
| maDg | id |

---

## HIGH PRIORITY FIX #7: Auth User Info Fields

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/modules/auth.js`

#### Fix 7.1 - Update login() method (Lines 10-25)
```javascript
// CURRENT:
api.setUser({
    username: loginData.username,
    role:     loginData.role,
    fullName: loginData.fullName || loginData.username,
    email:    loginData.email    || '',
    phone:    loginData.phone    || '',
    address:  loginData.address  || ''
});

// LIKELY FIX (verify against LoginResponse DTO):
api.setUser({
    username: loginData.username,
    role:     loginData.role,
    fullName: loginData.fullName || loginData.name || loginData.username,
    email:    loginData.email    || '',
    phone:    loginData.phone    || loginData.phoneNumber || '',
    address:  loginData.address  || loginData.shippingAddress || ''
});
```

**ACTION**: Check these BE files:
- `com/bookstore/dto/LoginResponse.java`
- `com/bookstore/service/AuthService.login()`

---

## MEDIUM PRIORITY FIX #8: Support Ticket Endpoint

### Location: `bookstoreonline/src/main/resources/static/web/assets/js/modules/hoTro.js`

#### Fix 8.1 - Check correct endpoint (Line 7)
```javascript
// CURRENT:
const res = await api.get(`/support?username=${user.username}`);

// LIKELY FIX (depends on BE endpoint):
const res = await api.get(`/support/user/${user.username}`);
// OR if query param is supported:
// const res = await api.get(`/support?username=${user.username}`);
```

**ACTION**: Check SupportTicketController:
- Confirm endpoint: `/api/support/user/{username}` or query param?

---

## Testing After Fixes

### Quick Test Checklist:
```
[ ] Login works - user info displays correctly
[ ] Checkout works - can create order
[ ] Payment works - can view payment history
[ ] Password change works - user can change password
[ ] Reviews work - can see and submit reviews
[ ] Admin dashboard works - all stats display
[ ] Support tickets work - filter by user
[ ] Categories work - dropdown populates
```

### Browser Console Checks:
```javascript
// Check API response format
// Open DevTools → Network tab
// Make API call and inspect response
// Should see: { status: 200, message: "...", data: {...} }
```

---

## Priority Implementation Order:

1. ✓ **Fix #1** - Payment endpoints (BLOCKING CHECKOUT)
2. ✓ **Fix #4** - Password change fields (BLOCKING USER)
3. ✓ **Fix #2** - Audit log fields (BLOCKING ADMIN)
4. ✓ **Fix #3** - Dashboard rankings (BLOCKING ADMIN)
5. ✓ **Fix #5** - API response standardization (FOUNDATION)
6. ✓ **Fix #6** - Review fields (BLOCKING REVIEWS)
7. ✓ **Fix #7** - Auth user fields (BLOCKING PROFILE)
8. ✓ **Fix #8** - Support endpoint (BLOCKING SUPPORT)

---

## Common Issues During Implementation

**Issue**: "Cannot read property 'xxx' of undefined"
**Solution**: Verify exact field names in BE DTOs

**Issue**: "API returns 404"
**Solution**: Check endpoint path - may have changed from Vietnamese to English

**Issue**: "Data displays as '[object Object]'"
**Solution**: Response format issue - use `api.parseResponse()`

---

**Status**: Ready for implementation
**Questions**: Check BE code for exact DTO field names before applying fixes
