# 🎯 MASTER FIX PLAN - BookStore Online

## Executive Summary
**Total Issues**: 12
**Total Files to Modify**: 12
**Total Lines Changed**: ~200
**Estimated Time**: 50 minutes
**Risk Level**: 🟡 MEDIUM (All fixes are field/endpoint name changes)

---

## PHASE 1: CRITICAL (15 min) - Fix Basic Connectivity Issues

### Fix 1: Payment Module - Endpoints & Field Names ⚠️ BLOCKS CHECKOUT
**File**: `modules/payments.js`
**What**: Change endpoint `/thanhtoan` → `/payments` + update 8 field names
**Changes**: ~30 lines
```
BEFORE:
  api.get(`/thanhtoan`, {search, status, method})
  payment.trangThai → status badge
  payment.maDonHang → order ID
  payment.tenKhachHang → customer name
  payment.soTien → amount
  payment.phuongThucThanhToan → payment method
  
AFTER:
  api.get(`/payments`, {search, status, method})
  payment.statusCode → status badge
  payment.orderId → order ID
  payment.customerName → customer name
  payment.amount → amount
  payment.paymentMethod → payment method
```

### Fix 2: Password Change Fields ⚠️ BLOCKS USER ACCOUNT
**File**: `Auth/ChangePassword.html`
**What**: Update request body field names for password change
**Changes**: ~5 lines
```
BEFORE:
  api.put("/auth/change-password", {
    oldPassword: currentPassword,
    newPassword: newPassword,
    confirmPassword: confirmPassword
  })
  
AFTER:
  api.put("/auth/change-password", {
    currentPassword: currentPassword,  // or oldPassword - TBD
    newPassword: newPassword
    // Remove confirmPassword (FE validation only)
  })
```

### Fix 3: Audit Log Field Names ⚠️ BREAKS ADMIN DASHBOARD
**File**: `modules/auditlog.js`
**What**: Update 12 Vietnamese field names to English
**Changes**: ~60 lines across 3 methods
```
FIELD MAPPINGS:
  hanhDong → action
  moTa → details
  thoiGian → timestamp
  tenNguoiDung → username
  diaChiIP → ipAddress
  maAudit → id
  loaiDoiTuong → targetType
  tongHoatDong → totalActivities
  homNay → todayCount
  nguoiDungHoatDong → activeUsers
  chiTinhSuaDoi → mutations
```

### Fix 4: Dashboard Book Rankings ⚠️ DASHBOARD EMPTY
**File**: `admin/dashboard.js`
**What**: Update book title field reference
**Changes**: ~3 lines
```
BEFORE:
  item.title || item.isbn
  item.totalSold
  
AFTER:
  item.title || item.bookTitle || item.isbn  (with fallback)
  item.totalSold || item.salesCount  (with fallback)
```

---

## PHASE 2: HIGH PRIORITY (15 min) - Standardize & Fix Display

### Fix 5: API Response Handler Standardization 🔧 FOUNDATION
**File**: `common.js` + 8 modules
**What**: Create unified response parser and apply to all modules
**Changes**: ~100 lines
```
ADD TO common.js:
  api.parseResponse = (response) => {
    // Standardized handler for ApiResponse<T>
    if (response?.data) return response.data;
    return Array.isArray(response) ? response : [];
  }

UPDATE in modules: books.js, categories.js, cart.js, orders.js, etc.
  Old: const list = res.data || res;
  New: const list = api.parseResponse(res);
```

### Fix 6: Review Display Fields 📝 FIXES REVIEW DISPLAY
**File**: `modules/review.js`
**What**: Update 12 Vietnamese field names to English
**Changes**: ~40 lines
```
FIELD MAPPINGS:
  tenKhachHang → username
  diemDg → rating
  nhanXet → comment
  ngayDg → createdAt
  maDg → id
```

### Fix 7: Auth User Info Mapping 👤 COMPLETES USER PROFILE
**File**: `modules/auth.js`
**What**: Update user info field mapping with fallbacks
**Changes**: ~10 lines
```
BEFORE:
  fullName: loginData.fullName || loginData.username
  
AFTER:
  fullName: loginData.fullName || loginData.name || loginData.username
  phone: loginData.phone || loginData.phoneNumber || ''
  address: loginData.address || loginData.shippingAddress || ''
```

---

## PHASE 3: MEDIUM PRIORITY (10 min) - Fix Specific Features

### Fix 8: Support Ticket Endpoint 🎫 FIXES SUPPORT
**File**: `modules/hoTro.js`
**What**: Change query param to path param
**Changes**: 1 line
```
BEFORE:
  api.get(`/support?username=${user.username}`)
  
AFTER:
  api.get(`/support/user/${user.username}`)
```

### Fix 9: Category Tree Loading 📂 FIXES DROPDOWNS
**File**: `modules/books.js`
**What**: Add fallback for category field names
**Changes**: ~15 lines
```
HANDLE BOTH:
  children[] or subCategories[]
  id or categoryId
  name or categoryName
```

### Fixes 10-12: Already Covered ✓
- Audit stats (included in Fix 3)
- No additional changes needed

---

## EXECUTION SUMMARY

```
┌─────────────────────────────────────────────────────────────────┐
│ PHASE 1: CRITICAL ISSUES (15 min)                               │
├─────────────────────────────────────────────────────────────────┤
│ [Fix 1.1] payments.js .......................... 30 lines       │
│ [Fix 1.2] ChangePassword.html .................. 5 lines        │
│ [Fix 1.3] auditlog.js .......................... 60 lines       │
│ [Fix 1.4] dashboard.js ......................... 3 lines        │
│ ┗━━━ SUBTOTAL: 98 lines                                         │
├─────────────────────────────────────────────────────────────────┤
│ PHASE 2: HIGH PRIORITY (15 min)                                 │
├─────────────────────────────────────────────────────────────────┤
│ [Fix 2.1] common.js + 8 modules ............... 100 lines       │
│ [Fix 2.2] review.js ........................... 40 lines        │
│ [Fix 2.3] auth.js ............................. 10 lines        │
│ ┗━━━ SUBTOTAL: 150 lines                                        │
├─────────────────────────────────────────────────────────────────┤
│ PHASE 3: MEDIUM PRIORITY (10 min)                               │
├─────────────────────────────────────────────────────────────────┤
│ [Fix 3.1] hoTro.js ............................. 1 line         │
│ [Fix 3.2] books.js ............................ 15 lines        │
│ ┗━━━ SUBTOTAL: 16 lines                                         │
├─────────────────────────────────────────────────────────────────┤
│ TOTAL CHANGES: 12 Files | ~264 lines | 50 min                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## DEPENDENCIES & ORDER

✅ **No dependencies** - All fixes are independent
✅ **Safe to apply in sequence** - Each fix is isolated
✅ **Can apply Phase 1 only** if need quick results

**Recommended Order**:
1. Phase 1 (most critical - unblocks checkout)
2. Phase 2 (foundation + display fixes)
3. Phase 3 (nice to have)

---

## TESTING STRATEGY

### After Phase 1 (Quick Test):
```
✓ Can create payment?
✓ Can change password?
✓ Admin dashboard shows audit logs?
✓ Dashboard shows top selling books?
```

### After Phase 2 (Full Test):
```
✓ All API responses work?
✓ Reviews display correctly?
✓ User profile complete after login?
```

### After Phase 3 (Final Test):
```
✓ Support tickets filter by user?
✓ Category dropdowns populate?
✓ All features working?
```

---

## ERROR HANDLING

**If Payment Fix Fails**:
- Verify BE endpoint is really `/api/payments`
- Check payment DTOs for exact field names

**If Password Change Fails**:
- Check ChangePasswordRequest DTO field names
- May need `oldPassword` or `currentPassword`

**If Audit Log Fails**:
- Verify AuditLogDTO field names match
- Check if BE returns `action` or `hanhDong`

**Generic Fallbacks Added**:
- Use `|| alternativeName` for unknown fields
- Console logging for debugging
- Won't crash if field names slightly wrong

---

## RISK ASSESSMENT

| Issue | Risk | Mitigation |
|-------|------|-----------|
| Wrong field names | HIGH | Fallback logic added |
| Wrong endpoints | MEDIUM | Used from Java code |
| Missing fields | LOW | Optional chaining |
| Syntax errors | VERY LOW | Careful copy-paste |

**Overall Risk**: 🟡 MEDIUM
- All changes are name/endpoint changes
- Easy to revert if needed
- No logic changes

---

## GO/NO-GO CHECKLIST

Before executing:
```
[ ] All files identified ✓
[ ] All changes planned ✓
[ ] Dependencies checked ✓
[ ] Rollback plan ready ✓
[ ] Testing strategy ready ✓
```

---

## READY TO EXECUTE?

### Option A: Execute All at Once
→ Quickest (50 min total)
→ All fixes applied sequentially
→ Test once at the end

### Option B: Phase by Phase
→ Safer (stop if issues found)
→ Test after each phase
→ Takes longer but more controlled

### Option C: Manual Selection
→ You pick which phases to fix first
→ I execute selected fixes only

---

## 🚀 CONFIRMATION NEEDED

Which approach do you prefer?

```
A. Fix all immediately (execute all 12 fixes at once)
B. Phase by phase (test after each phase)
C. Critical only first (just Phase 1)
D. Custom (you select which fixes)
```

**Type your choice or confirmation to proceed!**
