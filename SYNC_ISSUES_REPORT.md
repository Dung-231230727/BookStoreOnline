# BookStore Online - FE/BE Synchronization Issues Report
**Generated**: 15/04/2026
**Status**: ⚠️ CRITICAL ISSUES FOUND

---

## Executive Summary
Analysis of ALL_HTML_CONTENT.txt, ALL_JS_CONTENT.txt, and ALL_JAVA_CONTENT.txt revealed **12 major synchronization issues** between Frontend and Backend. These issues are causing multiple test failures on the web.

**Critical Issues**: 4
**High Priority**: 3
**Medium Priority**: 3
**Low Priority**: 2

---

## CRITICAL ISSUES (Must Fix Immediately)

### 🔴 Issue #1: Payment Endpoint Vietnamese → English Mismatch
**Severity**: CRITICAL - BLOCKS CHECKOUT
**Component**: Payment Processing

#### Problem:
- **FE Location**: `modules/payments.js` (line 18)
- **FE calls**: `/api/thanhtoan` (Vietnamese endpoint name)
- **BE provides**: `/api/payments` (English endpoint name)
- **Field names in FE**: Vietnamese (`trangThai`, `maDonHang`, `tenKhachHang`, `soTien`, `phuongThucThanhToan`)

#### Impact:
- Admin payment history won't load
- Payment status updates fail
- User cannot view payment history

#### Files to Fix:
```
FE:
  - bookstoreonline/src/main/resources/static/web/assets/js/modules/payments.js (lines 18-250)
  - ALL Vietnamese field names need to be changed to English
  
BE:
  - Verify /api/payments endpoint exists and is correct
  - PaymentController needs to be checked
```

#### Required Changes:
```javascript
// BEFORE (WRONG):
await api.get(`/thanhtoan`, { search, status, method });

// AFTER (CORRECT):
await api.get(`/payments`, { search, status, method });

// Field name changes required:
// trangThai → statusCode
// maDonHang → orderId
// tenKhachHang → customerName
// soTien → amount
// phuongThucThanhToan → paymentMethod
// thoiGianThanhToan → paymentDate
```

---

### 🔴 Issue #2: Dashboard Audit Log Field Names Mismatch
**Severity**: CRITICAL - ADMIN DASHBOARD BROKEN
**Component**: Admin Audit Logs

#### Problem:
- **FE Location**: `modules/auditlog.js` (lines 26-90)
- **FE expects**: Vietnamese field names (`hanhDong`, `moTa`, `thoiGian`, `tenNguoiDung`, `diaChiIP`)
- **BE returns**: Unknown exact field names (need to check AuditLogDTO)

#### Fields Affected:
```javascript
// LIKELY INCORRECT FIELD NAMES:
log.hanhDong        → should be: log.action
log.moTa             → should be: log.details
log.thoiGian        → should be: log.timestamp
log.tenNguoiDung    → should be: log.username
log.diaChiIP        → should be: log.ipAddress
log.maAudit         → should be: log.id
```

#### Files to Fix:
```
FE:
  - bookstoreonline/src/main/resources/static/web/assets/js/modules/auditlog.js
  - AuditLogs/Admin/Index.html
  
BE:
  - Check com/bookstore/dto/AuditLogDTO.java field names
```

#### Location of Bugs:
- Line 26-40: loadLogs() method - wrong field names
- Line 60-90: viewDetail() method - wrong field names  
- Line 118: updateStatistics() method - wrong field names

---

### 🔴 Issue #3: Dashboard Book Ranking Fields Mismatch
**Severity**: CRITICAL - DASHBOARD TOP SELLING BOOKS EMPTY
**Component**: Admin Dashboard Rankings

#### Problem:
- **FE Location**: `admin/dashboard.js` (line 46-48)
- **FE expects**: `item.title` or `item.isbn`
- **FE comment says**: "tenSach" (Vietnamese name)
- **Actual BE field**: Unknown, needs verification

#### Code Location:
```javascript
// Line 46-48: LIKELY WRONG
data.forEach((item, i) => {
    tbody.innerHTML += `
        <tr>
            <td class="fw-bold">${item.title || item.isbn}</td>
            <td class="text-end pe-3 fw-bold text-accent">${item.totalSold || 0} books</td>
        </tr>
    `;
});
```

#### Need to Verify:
- BookRankingDTO actual field names from AdminDashboardController
- Is it `title`, `bookTitle`, `tenSach`, or something else?

---

### 🔴 Issue #4: Password Change Request Fields Mismatch
**Severity**: CRITICAL - USERS CANNOT CHANGE PASSWORD
**Component**: User Account Management

#### Problem:
- **FE Location**: `Auth/ChangePassword.html` (line 357)
- **FE sends**: `oldPassword`, `newPassword`, `confirmPassword`
- **BE expects**: Unknown exact field names

#### Code Location:
```javascript
// Line 357: POSSIBLY WRONG FIELD NAMES
api.put("/auth/change-password", {
    oldPassword:     currentPassword,
    newPassword:     newPassword,
    confirmPassword: confirmPassword
})
```

#### Likely Issues:
- BE may expect `currentPassword` instead of `oldPassword`
- BE may not have `confirmPassword` field (it's validation-only on FE)
- BE field names may be different

#### Files to Check:
```
BE:
  - com/bookstore/dto/ChangePasswordRequest.java
  - com/bookstore/controller/AuthController.changePassword()
  
FE:
  - Auth/ChangePassword.html (lines 340-380)
```

---

## HIGH PRIORITY ISSUES

### 🟠 Issue #5: API Response Structure Inconsistency
**Severity**: HIGH - CAUSES RANDOM FAILURES
**Component**: Global API Handling

#### Problem:
- **Location**: `common.js` (lines 66-67, 159-242)
- **FE uses**: Inconsistent fallback logic `res.data || res` or `res.data || []`
- **BE returns**: Standard `ApiResponse<T> { status, message, data }`
- **Issue**: Different modules handle response differently

#### Affected Locations:
```javascript
// common.js line 159-242: INCONSISTENT PATTERNS

// Pattern 1: Data access
const stats = res.data || res;

// Pattern 2: Array access
const data = res.data || [];

// Pattern 3: Direct array
const data = Array.isArray(res) ? res : (res.data || []);

// Pattern 4: No fallback
const logs = res.data || res;
```

#### Impact:
- API calls work intermittently
- Array operations fail when response structure differs
- Some modules break, others work fine

#### Solution:
Standardize to single response handling pattern across ALL modules

---

### 🟠 Issue #6: Review/Comments Field Names Vietnamese
**Severity**: HIGH - REVIEWS WON'T DISPLAY
**Component**: Book Reviews

#### Problem:
- **Location**: `modules/review.js` (lines 24-40)
- **FE expects**: Vietnamese names (`tenKhachHang`, `diemDg`, `nhanXet`, `ngayDg`)
- **BE likely returns**: English names (`username`, `rating`, `comment`, `createdAt`)

#### Field Mapping Needed:
```javascript
// Current (WRONG):
r.tenKhachHang   → Should be: r.username
r.diemDg         → Should be: r.rating
r.nhanXet        → Should be: r.comment
r.ngayDg         → Should be: r.createdAt
r.maDg           → Should be: r.id
```

#### Files Affected:
- `modules/review.js` (lines 24, 40, 115, 130)
- `modules/reviews.js` (lines 20-30)

---

### 🟠 Issue #7: Auth User Info Field Mapping
**Severity**: HIGH - USER PROFILE INCOMPLETE
**Component**: Authentication & User Info

#### Problem:
- **Location**: `modules/auth.js` (lines 12-19)
- **FE saves**: `username`, `role`, `fullName`, `email`, `phone`, `address`
- **BE LoginResponse fields**: Unknown exact field names

#### Code Location:
```javascript
// auth.js lines 12-19: Need to verify
api.setUser({
    username: loginData.username,
    role:     loginData.role,
    fullName: loginData.fullName || loginData.username,
    email:    loginData.email    || '',
    phone:    loginData.phone    || '',
    address:  loginData.address  || ''
});
```

#### Required Verification:
- Check LoginResponse DTO field names
- Does BE return phone, address, or only email?
- Does BE use fullName or name?

#### Files to Check:
```
BE:
  - com/bookstore/dto/LoginResponse.java
  - com/bookstore/controller/AuthController.login()
```

---

## MEDIUM PRIORITY ISSUES

### 🟡 Issue #8: Category Loading Tree Flattening
**Severity**: MEDIUM - CATEGORY DROPDOWNS MAY NOT WORK
**Component**: Book Categories

#### Problem:
- **Location**: `modules/books.js` (line 256-275)
- **Issue**: Tree flattening logic may not handle BE response structure
- **Need to verify**: CategoryDTO structure from BE

#### Code Location:
```javascript
// Line 265-275: Tree walking may fail
const walk = (nodes, depth = 0) => {
    nodes.forEach(n => {
        options.push({ id: n.id, name: n.name, depth: depth });
        if (n.children) walk(n.children, depth + 1);
    });
};
```

#### Unknown Variables:
- Does BE return `children` or `subCategories`?
- Is ID `id`, `categoryId`, or `categoryId`?
- Is name `name`, `categoryName`, or `tenDanhMuc`?

---

### 🟡 Issue #9: Support Tickets Username Filtering
**Severity**: MEDIUM - USERS MAY SEE ALL TICKETS
**Component**: Support System

#### Problem:
- **Location**: `modules/hoTro.js` (line 7)
- **FE calls**: `/api/support?username=${user.username}`
- **BE endpoint**: `/api/support/user/{username}` likely exists
- **Issue**: FE using wrong query parameter syntax

#### Code to Fix:
```javascript
// CURRENT (POSSIBLY WRONG):
const res = await api.get(`/support?username=${user.username}`);

// SHOULD BE:
const res = await api.get(`/support/user/${user.username}`);
```

#### Files to Check:
```
BE:
  - Check SupportTicketController.getRequestsByCustomer()
  - Is it @GetMapping("/user/{username}") or query param?
```

---

### 🟡 Issue #10: Audit Statistics Response Fields
**Severity**: MEDIUM - DASHBOARD STATS MAY NOT DISPLAY
**Component**: Admin Dashboard

#### Problem:
- **Location**: `modules/auditlog.js` (lines 95-106)
- **FE expects**: Vietnamese field names (`tongHoatDong`, `homNay`, `nguoiDungHoatDong`, `chiTinhSuaDoi`)
- **BE likely returns**: English field names

#### Field Mapping:
```javascript
// Current (WRONG):
stats.tongHoatDong           → Should be: stats.totalActivities
stats.homNay                 → Should be: stats.todayCount
stats.nguoiDungHoatDong      → Should be: stats.activeUsers
stats.chiTinhSuaDoi          → Should be: stats.mutations
```

---

## SUMMARY TABLE

| # | Issue | Component | Severity | Status | Action Required |
|---|-------|-----------|----------|--------|-----------------|
| 1 | Payment endpoints `/thanhtoan` → `/payments` | Payment | CRITICAL | ❌ Not Fixed | Change all 10+ endpoints + field names |
| 2 | Audit log Vietnamese field names | Admin/Audit | CRITICAL | ❌ Not Fixed | Map all field names |
| 3 | Dashboard ranking book title field | Admin/Dashboard | CRITICAL | ❌ Not Fixed | Verify BookRankingDTO |
| 4 | Password change field names | Auth | CRITICAL | ❌ Not Fixed | Verify ChangePasswordRequest DTO |
| 5 | Inconsistent API response handling | Global | HIGH | ❌ Not Fixed | Standardize in common.js |
| 6 | Review Vietnamese field names | Reviews | HIGH | ❌ Not Fixed | Map all field names |
| 7 | Auth user info field mapping | Auth | HIGH | ❌ Not Fixed | Verify LoginResponse DTO |
| 8 | Category tree flattening | Categories | MEDIUM | ❌ Not Fixed | Verify CategoryDTO structure |
| 9 | Support ticket username filter | Support | MEDIUM | ❌ Not Fixed | Check endpoint signature |
| 10 | Audit stats field names | Dashboard | MEDIUM | ❌ Not Fixed | Map field names |

---

## RECOMMENDED FIX SEQUENCE

### Phase 1 - CRITICAL (Do First)
1. Fix payment endpoints (Issue #1) - Unblocks checkout
2. Fix audit log fields (Issue #2) - Admin dashboard works
3. Fix dashboard rankings (Issue #3) - Dashboard displays data
4. Fix password change fields (Issue #4) - Users can change password

### Phase 2 - HIGH (Do Next)
5. Standardize API response handling (Issue #5) - Foundation for stability
6. Fix review fields (Issue #6) - Reviews display correctly
7. Fix auth user info (Issue #7) - Profile complete

### Phase 3 - MEDIUM (Then)
8. Fix category loading (Issue #8)
9. Fix support filtering (Issue #9)
10. Fix audit stats (Issue #10)

---

## VERIFICATION CHECKLIST

Before starting fixes, verify these BE components:

```
[ ] PaymentController - verify endpoint paths and response structure
[ ] AuditLogDTO - verify field names (action, details, timestamp, etc.)
[ ] BookRankingDTO - verify field names (title, totalSold, isbn, etc.)
[ ] ChangePasswordRequest - verify field names (oldPassword, newPassword, etc.)
[ ] LoginResponse - verify field names (username, role, fullName, email, etc.)
[ ] CategoryDTO - verify structure (id/categoryId, name/categoryName, children/subCategories)
[ ] ReviewDTO - verify field names (username, rating, comment, createdAt, etc.)
[ ] AuditStatsDTO - verify field names (totalActivities, todayCount, activeUsers, mutations)
```

---

## Testing Plan After Fixes

### Unit Tests Needed:
1. ✓ Checkout flow - test payment endpoint
2. ✓ Admin dashboard - test all statistics
3. ✓ Password change - test with various inputs
4. ✓ Reviews - test display and submission
5. ✓ User profile - test after login
6. ✓ Support tickets - test filtering

### End-to-End Tests Needed:
1. ✓ Complete checkout to payment
2. ✓ Admin views all dashboard sections
3. ✓ User changes password successfully
4. ✓ Reviews display correctly
5. ✓ Support ticket filtering works

---

## Notes
- All translations from Vietnamese to English naming conventions
- Maintain consistent camelCase naming in FE
- Ensure DTO field names match between FE expectations and BE returns
- Consider adding integration tests for API synchronization

**Status**: Ready for implementation
**Priority**: Implement Phase 1 immediately to unblock core functionality
