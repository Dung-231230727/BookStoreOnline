-- ==============================================================================
-- DỮ LIỆU MẪU (SAMPLE DATA) CHO HỆ THỐNG NHÀ SÁCH ONLINE
-- Database: nha_sach_online (SQL Server)
-- CHẾ ĐỘ: RESET TOÀN BỘ (XÓA CŨ - NẠP MỚI)
-- ==============================================================================

USE [nha_sach_online];
GO

-- 0. XÓA DỮ LIỆU CŨ THEO THỨ TỰ RÀNG BUỘC (FK)
DELETE FROM [dbo].[audit_log];
DELETE FROM [dbo].[ho_tro];
DELETE FROM [dbo].[danh_gia];
DELETE FROM [dbo].[chi_tiet_phieu_xuat];
DELETE FROM [dbo].[phieu_xuat];
DELETE FROM [dbo].[van_chuyen];
DELETE FROM [dbo].[thanh_toan];
DELETE FROM [dbo].[chi_tiet_don_hang];
DELETE FROM [dbo].[chi_tiet_phieu_nhap];
DELETE FROM [dbo].[gio_hang];
DELETE FROM [dbo].[don_hang];
DELETE FROM [dbo].[phieu_nhap];
DELETE FROM [dbo].[kho_hang];
DELETE FROM [dbo].[sach_tac_gia];
DELETE FROM [dbo].[sach_vat_ly];
DELETE FROM [dbo].[sach_dien_tu];
DELETE FROM [dbo].[sach];
DELETE FROM [dbo].[nhan_vien];
DELETE FROM [dbo].[khach_hang];
DELETE FROM [dbo].[tai_khoan];
DELETE FROM [dbo].[danh_muc];
DELETE FROM [dbo].[nxb];
DELETE FROM [dbo].[tac_gia];
DELETE FROM [dbo].[nha_cung_cap];
DELETE FROM [dbo].[voucher];

-- RESET ID TỰ TĂNG (IDENTITY) VỀ 0
DBCC CHECKIDENT ('[dbo].[audit_log]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[ho_tro]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[danh_gia]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[gio_hang]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[nhan_vien]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[khach_hang]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[danh_muc]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[nxb]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[tac_gia]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[nha_cung_cap]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[kho_hang]', RESEED, 0);
GO

-- 0.1. LOOKUP TABLES (INSERT ENUM/STATUS VALUES)
INSERT INTO [dbo].[trang_thai_don_hang] ([ma_trang_thai]) VALUES
(N'MOI'), (N'DA_XAC_NHAN'), (N'CHO_LAY_HANG'), (N'DANG_GIAO'), (N'HOAN_TAT'), (N'DA_HUY');
GO

INSERT INTO [dbo].[trang_thai_thanh_toan] ([ma_trang_thai]) VALUES
(N'PENDING'), (N'SUCCESS'), (N'FAILED');
GO

INSERT INTO [dbo].[phuong_thuc_thanh_toan] ([ma_pt]) VALUES
(N'COD'), (N'MOMO'), (N'VNPAY');
GO

INSERT INTO [dbo].[trang_thai_ho_tro] ([ma_trang_thai]) VALUES
(N'OPEN'), (N'PROCESSING'), (N'CLOSED');
GO

-- 1. TAI_KHOAN (Mật khẩu mặc định: admin123)
-- Chú ý: Roles: 'ADMIN', 'STAFF', 'STOREKEEPER', 'CUSTOMER'
INSERT INTO [dbo].[tai_khoan] ([username], [password], [role], [trang_thai], [ngay_tao]) VALUES
('admin', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'ADMIN', 1, GETDATE()),
('staff1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STAFF', 1, GETDATE()),
('staff2', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STAFF', 1, GETDATE()),
('kho1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STOREKEEPER', 1, GETDATE()),
('customer1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 1, GETDATE()),
('customer2', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 1, GETDATE()),
('customer3', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 0, GETDATE());
GO

-- 2. NHAN_VIEN (Phần nhân sự)
INSERT INTO [dbo].[nhan_vien] ([username], [ho_ten], [sdt], [bo_phan]) VALUES
('admin', N'Nguyễn Quản Trị', '0901234567', 'QUAN_LY'),
('staff1', N'Trần Thị Bán Hàng', '0912345678', 'BAN_HANG'),
('staff2', N'Lê Văn Sales', '0923456789', 'BAN_HANG'),
('kho1', N'Phạm Thủ Kho', '0934567890', 'KHO');
GO

-- 3. KHACH_HANG (Phần khách hàng)
INSERT INTO [dbo].[khach_hang] ([username], [ho_ten], [sdt], [dia_chi_giao_hang], [diem_tich_luy]) VALUES
('customer1', N'Lê Minh Khôi', '0987123456', N'123 Đường Lê Lợi, Quận 1, TP.HCM', 150),
('customer2', N'Hoàng Thanh Trúc', '0987234567', N'456 Đường Nguyễn Huệ, Quận 3, TP.HCM', 50),
('customer3', N'Đặng Quốc Bảo', '0987345678', N'789 Đường CMT8, Quận Tân Bình, TP.HCM', 0);
GO

-- 4. DANH_MUC (Phân cấp cha-con)
INSERT INTO [dbo].[danh_muc] ([ten_danhmuc], [danh_muc_cha_id]) VALUES
(N'Sách Kinh Tế', NULL),      -- ID 1
(N'Sách Văn Học', NULL),      -- ID 2
(N'Sách Kỹ Năng', NULL),      -- ID 3
(N'Kinh Tế Học Cơ Bản', 1),   -- ID 4
(N'Tiểu Thuyết Trinh Thám', 2), -- ID 5
(N'Phát Triển Bản Thân', 3);  -- ID 6
GO

-- 5. NXB (Nhà xuất bản)
INSERT INTO [dbo].[nxb] ([ten_nxb]) VALUES
(N'NXB Trẻ'), (N'NXB Kim Đồng'), (N'NXB Giáo Dục'), (N'NXB Tổng Hợp TP.HCM'), (N'NXB Nhã Nam');
GO

-- 6. TAC_GIA (Tác giả)
INSERT INTO [dbo].[tac_gia] ([ten_tacgia], [tieu_su]) VALUES
(N'Nguyễn Nhật Ánh', N'Nhà văn nổi tiếng Việt Nam.'),
(N'Dale Carnegie', N'Tác giả cuốn Đắc Nhân Tâm.'),
(N'Conan Doyle', N'Cha đẻ Sherlock Holmes.'),
(N'Haruki Murakami', N'Nhà văn Nhật Bản.'),
(N'Paulo Coelho', N'Tác giả Nhà Giả Kim.');
GO

-- 7. SACH (Thông tin sách)
INSERT INTO [dbo].[sach] ([isbn], [ten_sach], [gia_niem_yet], [so_trang], [ma_danhmuc], [ma_nxb], [mo_ta_ngu_nghia], [anh_bia], [da_xoa]) VALUES
('9786041123456', N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 85000, 210, 2, 1, N'Tuổi thơ hồn nhiên.', 'chotoixinmotvedituoitho.jpg', 0),
('9780062315007', N'Nhà Giả Kim', 79000, 180, 2, 5, N'Hành trình vận mệnh.', 'nhagiakim.jpg', 0),
('9786045612345', N'Đắc Nhân Tâm', 95000, 320, 6, 4, N'Nghệ thuật lòng người.', 'dacnhantam.jpg', 0),
('9781234567890', N'Kinh Tế Học Cơ Bản', 150000, 450, 4, 3, N'Kinh tế học đại cương.', 'kinhtehoccoban.jpg', 0),
('9780987654321', N'Sherlock Holmes Toàn Tập', 250000, 1200, 5, 5, N'Trinh thám kinh điển.', 'sherlockholmestoantap.jpg', 0),
('9781111111111', N'Sách Cũ Lỗi Thời', 50000, 100, 1, 1, N'Dừng kinh doanh.', 'sachculoithoi.jpg', 1);
GO

-- 8. SACH_VAT_LY & SACH_DIEN_TU
INSERT INTO [dbo].[sach_vat_ly] ([isbn], [can_nang]) VALUES
('9786041123456', 0.35), ('9780062315007', 0.25), ('9786045612345', 0.45);
INSERT INTO [dbo].[sach_dien_tu] ([isbn], [dung_luong_file], [duong_dan_tai]) VALUES
('9781234567890', 5.5, 'https://cdn.com/9781234567890.pdf'),
('9780987654321', 12.0, 'https://cdn.com/9780987654321.epub');
GO

-- 9. SACH_TAC_GIA
INSERT INTO [dbo].[sach_tac_gia] ([isbn], [ma_tacgia]) VALUES
('9786041123456', 1), ('9780062315007', 5), ('9786045612345', 2), ('9781234567890', 2), ('9780987654321', 3);
GO

-- 10. NHA_CUNG_CAP
INSERT INTO [dbo].[nha_cung_cap] ([ten_ncc], [thong_tin_lien_he]) VALUES
(N'Phương Nam Book', N'TP.HCM'), (N'Fahasa', N'Quận 1'), (N'Tiền Phong', N'Hà Nội'), (N'Đại lý Miền Nam', N'TP.HCM'), (N'In Thăng Long', N'Đà Nẵng');
GO

-- 11. KHO_HANG
INSERT INTO [dbo].[kho_hang] ([isbn], [so_luong_ton], [vi_tri_ke], [nguong_bao_dong]) VALUES
('9786041123456', 100, 'A-12', 10), ('9780062315007', 50, 'B-05', 5), ('9786045612345', 3, 'A-01', 5),
('9781234567890', 200, 'C-01', 20), ('9780987654321', 30, 'B-10', 5);
GO

-- 12. VOUCHER
INSERT INTO [dbo].[voucher] ([ma_voucher], [gia_tri_giam], [dieu_kien_toi_thieu], [thoi_han]) VALUES
('GIAM20K', 20000, 200000, '2026-12-31'), ('FREESHIP', 30000, 500000, '2026-06-30'),
('WELCOME10', 10000, 0, '2026-12-31'), ('BLACKFRIDAY', 50000, 1000000, '2026-11-30'), ('MUAHEXANH', 15000, 150000, '2026-08-30');
GO

-- 13. DON_HANG & CHI_TIET_DON_HANG
INSERT INTO [dbo].[don_hang] ([ma_donhang], [ma_khachhang], [ma_voucher], [ngay_tao], [tong_tien_hang], [phi_vanchuyen], [tong_thanh_toan], [trang_thai], [dia_chi_giao_cu_the]) VALUES
('ORD-001', 1, 'GIAM20K', GETDATE(), 250000, 30000, 260000, 'HOAN_TAT', N'Phòng 502, HCM'),
('ORD-002', 2, NULL, GETDATE(), 85000, 20000, 105000, 'DANG_GIAO', N'Kiệt 12/4, HCM'),
('ORD-003', 1, 'FREESHIP', GETDATE(), 520000, 0, 520000, 'MOI', N'Văn phòng HCM'),
('ORD-004', 3, NULL, GETDATE(), 150000, 30000, 180000, 'DA_HUY', N'Số 1 Trần Phú'),
('ORD-005', 2, 'WELCOME10', GETDATE(), 95000, 20000, 105000, 'CHO_LAY_HANG', N'HCM');

INSERT INTO [dbo].[chi_tiet_don_hang] ([ma_donhang], [isbn], [so_luong], [gia_ban_chot]) VALUES
('ORD-001', '9786041123456', 2, 85000), ('ORD-001', '9780062315007', 1, 80000),
('ORD-002', '9786041123456', 1, 85000), ('ORD-003', '9780987654321', 2, 250000);
GO

-- 14. THANH_TOAN
INSERT INTO [dbo].[thanh_toan] ([ma_thanhtoan], [ma_donhang], [phuong_thuc], [trang_thai], [ngay_thanh_toan], [ma_tham_chieu_cong]) VALUES
('PAY001', 'ORD-001', 'COD', 'SUCCESS', GETDATE(), NULL),
('PAY002', 'ORD-002', 'MOMO', 'SUCCESS', GETDATE(), 'MOMO_01'),
('PAY003', 'ORD-003', 'VNPAY', 'PENDING', NULL, NULL);
GO

-- 15. AUDIT_LOG
INSERT INTO [dbo].[audit_log] ([username], [hanh_dong], [thoi_diem], [chi_tiet]) VALUES
('admin', N'Cập nhật tồn kho', GETDATE(), N'Sách 9786041123456 +50'),
('staff1', N'Xác nhận đơn', GETDATE(), N'Đơn ORD-001');
GO

-- 16. DANH_GIA
INSERT INTO [dbo].[danh_gia] ([ma_khachhang], [isbn], [diem_dg], [nhan_xet], [ngay_dg]) VALUES
(1, '9786041123456', 5, N'Sách rất hay!', GETDATE()),
(2, '9780062315007', 4, N'Nội dung sâu sắc.', GETDATE());
GO

-- 17. HO_TRO
INSERT INTO [dbo].[ho_tro] ([ma_khachhang], [tieu_de], [noi_dung], [trang_thai], [thoi_gian]) VALUES
(1, N'Hỏi đơn hàng', N'Khi nào giao?', 'OPEN', GETDATE());
GO