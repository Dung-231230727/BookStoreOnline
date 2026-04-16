-- ==============================================================================
-- STANDARDIZED SAMPLE DATA FOR BOOKSTORE ONLINE SYSTEM
-- Database: nha_sach_online (SQL Server)
-- MODE: FULL RESET (DELETE OLD -> INSERT NEW)
-- GUARANTEE: Explicit Identity Insertion
-- ==============================================================================

USE [nha_sach_online];
GO

-- 0. DELETE OLD DATA IN REVERSE FK ORDER
DELETE FROM [dbo].[inventory_logs];
DELETE FROM [dbo].[audit_logs];
DELETE FROM [dbo].[support_tickets];
DELETE FROM [dbo].[reviews];
DELETE FROM [dbo].[carts];
DELETE FROM [dbo].[shipments];
DELETE FROM [dbo].[payments];
DELETE FROM [dbo].[export_order_details];
DELETE FROM [dbo].[export_orders];
DELETE FROM [dbo].[order_details];
DELETE FROM [dbo].[orders];
DELETE FROM [dbo].[vouchers];
DELETE FROM [dbo].[purchase_order_details];
DELETE FROM [dbo].[purchase_orders];
DELETE FROM [dbo].[inventory];
DELETE FROM [dbo].[suppliers];
DELETE FROM [dbo].[physical_books];
DELETE FROM [dbo].[ebooks];
DELETE FROM [dbo].[book_authors];
DELETE FROM [dbo].[books];
DELETE FROM [dbo].[authors];
DELETE FROM [dbo].[publishers];
DELETE FROM [dbo].[categories];
DELETE FROM [dbo].[staff];
DELETE FROM [dbo].[customers];
DELETE FROM [dbo].[accounts];

-- RESET IDENTITY SEEDS
DBCC CHECKIDENT ('[dbo].[audit_logs]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[support_tickets]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[reviews]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[staff]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[customers]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[categories]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[publishers]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[authors]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[suppliers]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[inventory]', RESEED, 0);
GO

-- 1. ACCOUNTS (Default Password: admin123)
INSERT INTO [dbo].[accounts] ([username], [password], [role], [status], [created_at]) VALUES
('admin', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'ADMIN', 'ACTIVE', GETDATE()),
('staff1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STAFF', 'ACTIVE', GETDATE()),
('staff2', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STAFF', 'ACTIVE', GETDATE()),
('kho1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'STOREKEEPER', 'ACTIVE', GETDATE()),
('customer1', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 'ACTIVE', GETDATE()),
('customer2', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 'ACTIVE', GETDATE()),
('customer3', '$2a$10$hn.nR/A4Jw0/sgMHZfu4UeIv5owT5mTcpLVHgnHj4xMK4CT1z0YOu', 'CUSTOMER', 'DISABLED', GETDATE());
GO

-- 2. STAFF (staff_id)
SET IDENTITY_INSERT [dbo].[staff] ON;
INSERT INTO [dbo].[staff] ([staff_id], [username], [full_name], [phone], [department]) VALUES
(1, 'admin', N'Nguyen Quan Tri', '0901234567', 'MANAGEMENT'),
(2, 'staff1', N'Tran Thi Ban Hang', '0912345678', 'SALES'),
(3, 'staff2', N'Le Van Sales', '0923456789', 'SALES'),
(4, 'kho1', N'Pham Thu Kho', '0934567890', 'WAREHOUSE');
SET IDENTITY_INSERT [dbo].[staff] OFF;
GO

-- 3. CUSTOMERS (customer_id)
SET IDENTITY_INSERT [dbo].[customers] ON;
INSERT INTO [dbo].[customers] ([customer_id], [username], [full_name], [phone], [loyalty_points], [shipping_address]) VALUES
(1, 'customer1', N'Le Minh Khoi', '0987123456', 150, N'Office HCM, 123 Dist 1'),
(2, 'customer2', N'Hoang Thanh Truc', '0987234567', 50, N'Home HCM, Alley 45 Dist 3'),
(3, 'customer3', N'Dang Quoc Bảo', '0987345678', 0, N'N/A');
SET IDENTITY_INSERT [dbo].[customers] OFF;
GO

-- 4. CATEGORIES (category_id)
SET IDENTITY_INSERT [dbo].[categories] ON;
INSERT INTO [dbo].[categories] ([category_id], [category_name], [parent_id]) VALUES
(1, N'Economics', NULL),
(2, N'Literature', NULL),
(3, N'Skills', NULL),
(4, N'Basic Economics', 1),
(5, N'Detective Novels', 2),
(6, N'Self-Development', 3);
SET IDENTITY_INSERT [dbo].[categories] OFF;
GO

-- 5. PUBLISHERS (publisher_id)
SET IDENTITY_INSERT [dbo].[publishers] ON;
INSERT INTO [dbo].[publishers] ([publisher_id], [publisher_name]) VALUES
(1, N'Youth Publisher'), (2, N'Kim Dong'), (3, N'Education'), (4, N'General TP.HCM'), (5, N'Nha Nam');
SET IDENTITY_INSERT [dbo].[publishers] OFF;
GO

-- 6. AUTHORS (author_id)
SET IDENTITY_INSERT [dbo].[authors] ON;
INSERT INTO [dbo].[authors] ([author_id], [author_name], [biography]) VALUES
(1, N'Nguyen Nhat Anh', N'Famous Vietnamese writer.'),
(2, N'Dale Carnegie', N'Author of How to Win Friends and Influence People.'),
(3, N'Conan Doyle', N'Father of Sherlock Holmes.'),
(4, N'Haruki Murakami', N'Japanese writer.'),
(5, N'Paulo Coelho', N'Author of The Alchemist.');
SET IDENTITY_INSERT [dbo].[authors] OFF;
GO

-- 7. BOOKS
INSERT INTO [dbo].[books] ([isbn], [title], [price], [category_id], [publisher_id], [description], [cover_image], [status]) VALUES
('9786041123456', N'Cho Toi Xin Mot Ve Di Tuoi Tho', 85000, 2, 2, N'Childhood memories.', 'chotoixinmotvedituoitho.jpg', 'ACTIVE'),
('9780062315007', N'The Alchemist', 79000, 2, 5, N'Journey of destiny.', 'nhagiakim.jpg', 'ACTIVE'),
('9786045612345', N'How to Win Friends and Influence People', 95000, 6, 4, N'Art of dealing with people.', 'dacnhantam.jpg', 'ACTIVE'),
('9781234567890', N'Basic Economics', 150000, 4, 3, N'General economics.', 'kinhtehoccoban.jpg', 'ACTIVE'),
('9780987654321', N'Sherlock Holmes Collection', 250000, 5, 5, N'Classic detective.', 'sherlockholmestoantap.jpg', 'ACTIVE'),
('9781111111111', N'Outdated Book', 50000, 1, 1, N'Discontinued.', 'sachculoithoi.jpg', 'INACTIVE');
GO

-- 8. PHYSICAL & EBOOKS
INSERT INTO [dbo].[physical_books] ([isbn], [weight]) VALUES
('9786041123456', 0.35), ('9780062315007', 0.25), ('9786045612345', 0.45);
INSERT INTO [dbo].[ebooks] ([isbn], [file_size], [download_url]) VALUES
('9781234567890', 5.5, 'https://cdn.com/9781234567890.pdf'),
('9780987654321', 12.0, 'https://cdn.com/9780987654321.epub');
GO

-- 9. BOOK_AUTHORS
INSERT INTO [dbo].[book_authors] ([isbn], [author_id]) VALUES
('9786041123456', 1), ('9780062315007', 5), ('9786045612345', 2), ('9781234567890', 2), ('9780987654321', 3);
GO

-- 10. SUPPLIERS (supplier_id)
SET IDENTITY_INSERT [dbo].[suppliers] ON;
INSERT INTO [dbo].[suppliers] ([supplier_id], [supplier_name], [contact_info]) VALUES
(1, N'Phuong Nam Book', N'TP.HCM'), (2, N'Fahasa', N'District 1'), (3, N'Tien Phong', N'Ha Noi'), (4, N'Southern Agent', N'TP.HCM'), (5, N'Thang Long Printing', N'Da Nang');
SET IDENTITY_INSERT [dbo].[suppliers] OFF;
GO

-- 11. INVENTORY
INSERT INTO [dbo].[inventory] ([isbn], [stock_quantity], [shelf_location], [alert_threshold], [status]) VALUES
('9786041123456', 100, 'A-12', 10, 'AVAILABLE'), 
('9780062315007', 50, 'B-05', 5, 'AVAILABLE'), 
('9786045612345', 3, 'A-01', 5, 'AVAILABLE'),
('9781234567890', 200, 'C-01', 20, 'AVAILABLE'), 
('9780987654321', 30, 'B-10', 5, 'AVAILABLE');
GO

-- 12. VOUCHERS
INSERT INTO [dbo].[vouchers] ([voucher_code], [discount_value], [min_condition], [expiry_date]) VALUES
('SALE20K', 20000, 200000, '2026-12-31'), ('FREESHIP', 30000, 500000, '2026-06-30'),
('WELCOME10', 10000, 0, '2026-12-31'), ('BLACKFRIDAY', 50000, 1000000, '2026-11-30'), ('SUMMER2026', 15000, 150000, '2026-08-30');
GO

-- 13. ORDERS & DETAILS
INSERT INTO [dbo].[orders] ([order_id], [customer_id], [voucher_code], [created_at], [total_items_price], [shipping_fee], [total_payment], [status], [shipping_address]) VALUES
('ORD-001', 1, 'SALE20K', GETDATE(), 250000, 30000, 260000, 'COMPLETED', N'Room 502, HCM'),
('ORD-002', 2, NULL, GETDATE(), 85000, 20000, 105000, 'SHIPPING', N'Alley 12/4, HCM'),
('ORD-003', 1, 'FREESHIP', GETDATE(), 520000, 0, 520000, 'NEW', N'Office HCM'),
('ORD-004', 3, NULL, GETDATE(), 150000, 30000, 180000, 'CANCELLED', N'No 1 Tran Phu'),
('ORD-005', 2, 'WELCOME10', GETDATE(), 95000, 20000, 115000, 'PICKING', N'HCM');

INSERT INTO [dbo].[order_details] ([order_id], [isbn], [quantity], [final_price]) VALUES
('ORD-001', '9786041123456', 2, 85000), ('ORD-001', '9780062315007', 1, 80000),
('ORD-002', '9786041123456', 1, 85000), ('ORD-003', '9780987654321', 2, 250000);
GO

-- 14. PAYMENTS
INSERT INTO [dbo].[payments] ([payment_id], [order_id], [payment_method], [status], [payment_date], [transaction_reference]) VALUES
('PAY001', 'ORD-001', 'COD', 'SUCCESS', GETDATE(), NULL),
('PAY002', 'ORD-002', 'MOMO', 'SUCCESS', GETDATE(), 'MOMO_01'),
('PAY003', 'ORD-003', 'VNPAY', 'PENDING', NULL, NULL);
GO

-- 15. AUDIT_LOGS
INSERT INTO [dbo].[audit_logs] ([username], [action], [timestamp], [details]) VALUES
('admin', N'Stock Update', GETDATE(), N'Book 9786041123456 +50'),
('staff1', N'Order Confirmed', GETDATE(), N'Order ORD-001');
GO

-- 16. REVIEWS
SET IDENTITY_INSERT [dbo].[reviews] ON;
INSERT INTO [dbo].[reviews] ([review_id], [customer_id], [isbn], [rating], [comment], [review_date]) VALUES
(1, 1, '9786041123456', 5, N'Great book!', GETDATE()),
(2, 2, '9780062315007', 4, N'Very profound.', GETDATE());
SET IDENTITY_INSERT [dbo].[reviews] OFF;
GO

-- 17. SUPPORT_TICKETS
SET IDENTITY_INSERT [dbo].[support_tickets] ON;
INSERT INTO [dbo].[support_tickets] ([ticket_id], [customer_id], [title], [content], [status], [created_at]) VALUES
(1, 1, N'Order Inquiry', N'When will it arrive?', 'OPEN', GETDATE());
SET IDENTITY_INSERT [dbo].[support_tickets] OFF;
GO
