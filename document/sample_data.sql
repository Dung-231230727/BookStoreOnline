-- ==============================================================================
-- DỮ LIỆU MẪU ĐẦY ĐỦ (ĐÃ RESET IDENTITY SEED)
-- ==============================================================================

-- BƯỚC 1: XÓA DỮ LIỆU CŨ
DELETE FROM audit_log;
DELETE FROM ho_tro;
DELETE FROM danh_gia;
DELETE FROM gio_hang;
DELETE FROM chi_tiet_don_hang;
DELETE FROM don_hang;
DELETE FROM chi_tiet_phieu_nhap;
DELETE FROM phieu_nhap;
DELETE FROM kho_hang;
DELETE FROM sach_tac_gia;
DELETE FROM sach_vat_ly;
DELETE FROM sach_dien_tu;
DELETE FROM sach;
DELETE FROM tac_gia;
DELETE FROM nxb;
DELETE FROM danh_muc;
DELETE FROM nhan_vien;
DELETE FROM khach_hang;
DELETE FROM tai_khoan;
DELETE FROM voucher;
DELETE FROM nha_cung_cap;

-- BƯỚC 2: RESET IDENTITY (Để ID bắt đầu lại từ 1)
DBCC CHECKIDENT ('nhan_vien', RESEED, 0);
DBCC CHECKIDENT ('khach_hang', RESEED, 0);
DBCC CHECKIDENT ('danh_muc', RESEED, 0);
DBCC CHECKIDENT ('nxb', RESEED, 0);
DBCC CHECKIDENT ('tac_gia', RESEED, 0);
DBCC CHECKIDENT ('nha_cung_cap', RESEED, 0);
DBCC CHECKIDENT ('kho_hang', RESEED, 0);
DBCC CHECKIDENT ('danh_gia', RESEED, 0);
DBCC CHECKIDENT ('ho_tro', RESEED, 0);

-- BƯỚC 3: NẠP DỮ LIỆU MỚI
-- 1. TÀI KHOẢN (Password: 123456)
INSERT INTO tai_khoan (username, password, role, trang_thai) VALUES 
('admin', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'ADMIN', 1),
('nhanvien1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STAFF', 1),
('khachhang1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 1),
('khachhang2', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 1);

-- 2. NHÂN VIÊN & KHÁCH HÀNG (Sẽ nhận ID 1, 2...)
INSERT INTO nhan_vien (username, ho_ten, sdt, bo_phan) VALUES 
('nhanvien1', N'Nguyễn Văn Thành', '0987654321', 'BAN_HANG');

INSERT INTO khach_hang (username, ho_ten, sdt, dia_chi_giao_hang, diem_tich_luy) VALUES 
('khachhang1', N'Trần Thị Lan', '0123456789', N'123 Cầu Giấy, Hà Nội', 100),
('khachhang2', N'Lê Minh Hoàng', '0912345678', N'456 Quận 1, TP.HCM', 50);

-- 3. DANH MỤC, NXB, TÁC GIẢ (Sẽ nhận ID 1, 2, 3...)
INSERT INTO danh_muc (ten_danhmuc) VALUES (N'Văn học'), (N'Kinh tế'), (N'Công nghệ');
INSERT INTO nxb (ten_nxb) VALUES (N'NXB Trẻ'), (N'NXB Kim Đồng'), (N'NXB Giáo Dục');
INSERT INTO tac_gia (ten_tacgia) VALUES (N'Nguyễn Nhật Ánh'), (N'Dale Carnegie'), (N'Robert Kiyosaki');

-- 4. SÁCH (Sử dụng ID 1, 2, 3 đã reset ở trên)
INSERT INTO sach (isbn, ten_sach, gia_niem_yet, so_trang, ma_danhmuc, ma_nxb) 
VALUES ('9781111111111', N'Mắt Biếc', 100000, 300, 1, 1);
INSERT INTO sach_vat_ly (isbn, can_nang) VALUES ('9781111111111', 0.5);

INSERT INTO sach (isbn, ten_sach, gia_niem_yet, so_trang, ma_danhmuc, ma_nxb) 
VALUES ('9782222222222', N'Đắc Nhân Tâm', 150000, 400, 2, 2);
INSERT INTO sach_vat_ly (isbn, can_nang) VALUES ('9782222222222', 0.6);

INSERT INTO sach (isbn, ten_sach, gia_niem_yet, so_trang, ma_danhmuc, ma_nxb) 
VALUES ('9783333333333', N'Dạy Con Làm Giàu', 120000, 500, 2, 3);
INSERT INTO sach_vat_ly (isbn, can_nang) VALUES ('9783333333333', 0.7);

-- 5. ĐƠN HÀNG (Sử dụng ma_khachhang = 1, 2)
INSERT INTO don_hang (ma_donhang, ma_khachhang, tong_tien_hang, tong_thanh_toan, trang_thai, ngay_tao) 
VALUES ('DH001', 1, 250000, 250000, 'HOAN_TAT', '2024-04-10');
INSERT INTO chi_tiet_don_hang (ma_donhang, isbn, so_luong, gia_ban_chot) VALUES ('DH001', '9781111111111', 2, 100000);
INSERT INTO chi_tiet_don_hang (ma_donhang, isbn, so_luong, gia_ban_chot) VALUES ('DH001', '9782222222222', 1, 150000);

INSERT INTO don_hang (ma_donhang, ma_khachhang, tong_tien_hang, tong_thanh_toan, trang_thai, ngay_tao) 
VALUES ('DH002', 2, 500000, 500000, 'HOAN_TAT', '2024-04-12');
INSERT INTO chi_tiet_don_hang (ma_donhang, isbn, so_luong, gia_ban_chot) VALUES ('DH002', '9781111111111', 5, 100000);

INSERT INTO don_hang (ma_donhang, ma_khachhang, tong_tien_hang, tong_thanh_toan, trang_thai, ngay_tao) 
VALUES ('DH003', 1, 120000, 120000, 'MOI', '2024-04-13');
INSERT INTO chi_tiet_don_hang (ma_donhang, isbn, so_luong, gia_ban_chot) VALUES ('DH003', '9783333333333', 1, 120000);

-- 6. AUDIT LOGS
INSERT INTO audit_log (username, hanh_dong, chi_tiet, thoi_diem) VALUES 
('admin', 'UPDATE_ROLE', N'Thay đổi quyền user khachhang1 thành STAFF', GETDATE()),
('admin', 'LOCK_ACCOUNT', N'Khóa tài khoản khachhang2 do vi phạm', GETDATE());

-- 7. VOUCHER & DATA KHÁC
INSERT INTO voucher (ma_voucher, gia_tri_giam, thoi_han) VALUES ('GIAM20K', 20000, '2025-12-31');
INSERT INTO danh_gia (ma_khachhang, isbn, diem_dg, nhan_xet) VALUES (1, '9781111111111', 5, N'Sách tuyệt vời');
INSERT INTO ho_tro (ma_khachhang, tieu_de, noi_dung, trang_thai) VALUES (1, N'Cần giúp đỡ', N'Giao hàng hơi chậm', 'OPEN');
