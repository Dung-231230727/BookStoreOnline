USE [master]
GO

IF DB_ID('nha_sach_online') IS NOT NULL 
    DROP DATABASE nha_sach_online;
GO

CREATE DATABASE nha_sach_online;
GO

USE nha_sach_online;
GO

/* =============================================================================
   1. LOOKUP TABLES (STATUS TABLES)
============================================================================= */
CREATE TABLE order_status (status_code NVARCHAR(50) PRIMARY KEY);
INSERT INTO order_status VALUES 
(N'NEW'), (N'CONFIRMED'), (N'PICKING'), (N'SHIPPING'), (N'COMPLETED'), (N'CANCELLED');

CREATE TABLE payment_status (status_code NVARCHAR(50) PRIMARY KEY);
INSERT INTO payment_status VALUES 
(N'PENDING'), (N'SUCCESS'), (N'FAILED');

CREATE TABLE payment_methods (method_code NVARCHAR(50) PRIMARY KEY);
INSERT INTO payment_methods VALUES 
(N'COD'), (N'MOMO'), (N'VNPAY');

CREATE TABLE support_status (status_code NVARCHAR(50) PRIMARY KEY);
INSERT INTO support_status VALUES 
(N'OPEN'), (N'PROCESSING'), (N'CLOSED');

/* =============================================================================
   2. ACCOUNTS (RBAC)
============================================================================= */
CREATE TABLE accounts (
    username NVARCHAR(50) PRIMARY KEY,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(50) NOT NULL 
        CHECK (role IN (N'ADMIN', N'STAFF', N'STOREKEEPER', N'CUSTOMER')),
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT CK_password CHECK (LEN(password) >= 6)
);

/* =============================================================================
   3. USERS (IDENTITIES)
============================================================================= */
CREATE TABLE customers (
    customer_id BIGINT IDENTITY PRIMARY KEY,
    username NVARCHAR(50) UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(15),
    loyalty_points INT DEFAULT 0 CHECK (loyalty_points >= 0),
    shipping_address NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (username) REFERENCES accounts(username) ON DELETE CASCADE,
    CONSTRAINT UQ_customer_phone UNIQUE(phone)
);

CREATE TABLE staff (
    staff_id INT IDENTITY PRIMARY KEY,
    username NVARCHAR(50) UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(15),
    department NVARCHAR(50) CHECK (department IN (N'MANAGEMENT', N'SALES', N'WAREHOUSE')),
    created_at DATETIME DEFAULT GETDATE(),
    deleted_at DATETIME NULL,
    FOREIGN KEY (username) REFERENCES accounts(username) ON DELETE CASCADE,
    CONSTRAINT UQ_staff_phone UNIQUE(phone)
);

/* =============================================================================
   4. CATEGORIES & BOOKS
============================================================================= */
CREATE TABLE categories (
    category_id INT IDENTITY PRIMARY KEY,
    category_name NVARCHAR(100) NOT NULL,
    parent_id INT NULL,
    FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);

CREATE TABLE publishers (
    publisher_id INT IDENTITY PRIMARY KEY,
    publisher_name NVARCHAR(150) NOT NULL
);

CREATE TABLE books (
    isbn NVARCHAR(13) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category_id INT,
    publisher_id INT,
    description NVARCHAR(MAX),
    cover_image NVARCHAR(255),
    is_deleted BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id)
);

CREATE TABLE authors (
    author_id INT IDENTITY PRIMARY KEY,
    author_name NVARCHAR(100) NOT NULL,
    biography NVARCHAR(MAX)
);

CREATE TABLE book_authors (
    isbn NVARCHAR(13),
    author_id INT,
    PRIMARY KEY (isbn, author_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn),
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
);

CREATE TABLE ebooks (
    isbn NVARCHAR(13) PRIMARY KEY,
    file_size DECIMAL(10,2),
    download_url NVARCHAR(255),
    FOREIGN KEY (isbn) REFERENCES books(isbn) ON DELETE CASCADE
);

CREATE TABLE physical_books (
    isbn NVARCHAR(13) PRIMARY KEY,
    weight DECIMAL(5,2),
    FOREIGN KEY (isbn) REFERENCES books(isbn) ON DELETE CASCADE
);

/* =============================================================================
   5. WAREHOUSE & INVENTORY
============================================================================= */
CREATE TABLE suppliers (
    supplier_id INT IDENTITY PRIMARY KEY,
    supplier_name NVARCHAR(150) NOT NULL,
    contact_info NVARCHAR(MAX)
);

CREATE TABLE inventory (
    inventory_id INT IDENTITY PRIMARY KEY,
    isbn NVARCHAR(13) UNIQUE NOT NULL,
    stock_quantity INT DEFAULT 0 CHECK (stock_quantity >= 0),
    shelf_location NVARCHAR(50),
    alert_threshold INT DEFAULT 5,
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE purchase_orders (
    purchase_order_id NVARCHAR(20) PRIMARY KEY,
    supplier_id INT NOT NULL,
    staff_id INT NOT NULL,
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(15,2) DEFAULT 0,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE purchase_order_details (
    purchase_order_id NVARCHAR(20),
    isbn NVARCHAR(13),
    quantity INT NOT NULL CHECK (quantity > 0),
    purchase_price DECIMAL(12,2) NOT NULL CHECK (purchase_price >= 0),
    PRIMARY KEY (purchase_order_id, isbn),
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

/* =============================================================================
   6. VOUCHERS & ORDERS
============================================================================= */
CREATE TABLE vouchers (
    voucher_code NVARCHAR(20) PRIMARY KEY,
    discount_value DECIMAL(10,2) NOT NULL CHECK (discount_value >= 0),
    min_condition DECIMAL(12,2) DEFAULT 0,
    expiry_date DATETIME NOT NULL
);

CREATE TABLE orders (
    order_id NVARCHAR(20) PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    voucher_code NVARCHAR(20),
    created_at DATETIME DEFAULT GETDATE(),
    total_items_price DECIMAL(12,2) DEFAULT 0,
    shipping_fee DECIMAL(10,2) DEFAULT 0 CHECK (shipping_fee >= 0),
    total_payment DECIMAL(15,2) DEFAULT 0,
    status_code NVARCHAR(50) DEFAULT N'NEW',
    shipping_address NVARCHAR(MAX),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (voucher_code) REFERENCES vouchers(voucher_code),
    FOREIGN KEY (status_code) REFERENCES order_status(status_code)
);

CREATE TABLE order_details (
    order_id NVARCHAR(20),
    isbn NVARCHAR(13),
    quantity INT NOT NULL CHECK (quantity > 0),
    final_price DECIMAL(12,2) NOT NULL CHECK (final_price >= 0),
    PRIMARY KEY (order_id, isbn),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

/* =============================================================================
   7. WAREHOUSE EXPORT
============================================================================= */
CREATE TABLE export_orders (
    export_order_id NVARCHAR(20) PRIMARY KEY,
    order_id NVARCHAR(20) UNIQUE NOT NULL,
    export_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE export_order_details (
    export_order_id NVARCHAR(20),
    isbn NVARCHAR(13),
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (export_order_id, isbn),
    FOREIGN KEY (export_order_id) REFERENCES export_orders(export_order_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

/* =============================================================================
   8. PAYMENTS & SHIPPING
============================================================================= */
CREATE TABLE payments (
    payment_id NVARCHAR(50) PRIMARY KEY,
    order_id NVARCHAR(20) NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    status_code NVARCHAR(50) DEFAULT N'PENDING',
    payment_date DATETIME,
    transaction_reference NVARCHAR(100),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (payment_method) REFERENCES payment_methods(method_code),
    FOREIGN KEY (status_code) REFERENCES payment_status(status_code)
);

CREATE TABLE shipments (
    shipment_id NVARCHAR(30) PRIMARY KEY,
    order_id NVARCHAR(20) UNIQUE NOT NULL,
    carrier NVARCHAR(50) DEFAULT N'GHN',
    tracking_number NVARCHAR(50),
    tracking_status NVARCHAR(100),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

/* =============================================================================
   9. AUXILIARY (CARTS, REVIEWS, SUPPORT, AUDIT)
============================================================================= */
CREATE TABLE carts (
    customer_id BIGINT,
    isbn NVARCHAR(13),
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    PRIMARY KEY (customer_id, isbn),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE reviews (
    review_id BIGINT IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    isbn NVARCHAR(13) NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(MAX),
    review_date DATETIME DEFAULT GETDATE(),
    UNIQUE(customer_id, isbn),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE support_tickets (
    ticket_id BIGINT IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    title NVARCHAR(200) NOT NULL,
    content NVARCHAR(MAX),
    status_code NVARCHAR(50) DEFAULT N'OPEN',
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (status_code) REFERENCES support_status(status_code)
);

CREATE TABLE audit_logs (
    log_id BIGINT IDENTITY PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    action NVARCHAR(255) NOT NULL,
    details NVARCHAR(MAX),
    timestamp DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (username) REFERENCES accounts(username)
);

CREATE TABLE inventory_logs (
    log_id BIGINT IDENTITY PRIMARY KEY,
    isbn NVARCHAR(13) NOT NULL,
    staff_id INT NULL,
    change_type NVARCHAR(50) CHECK (change_type IN (N'IMPORT', N'EXPORT', N'RETURN', N'ADJUST')),
    quantity_changed INT NOT NULL,
    quantity_after INT NOT NULL,
    timestamp DATETIME DEFAULT GETDATE(),
    notes NVARCHAR(MAX),
    FOREIGN KEY (isbn) REFERENCES books(isbn),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

/* =============================================================================
   10. INDEXES
============================================================================= */
CREATE INDEX IX_orders_customer ON orders(customer_id);
CREATE INDEX IX_order_details_isbn ON order_details(isbn);
CREATE INDEX IX_books_category ON books(category_id);
CREATE INDEX IX_payments_order ON payments(order_id);
CREATE INDEX IX_inventory_isbn ON inventory(isbn);
CREATE INDEX IX_audit_username ON audit_logs(username);

/* =============================================================================
   11. TRIGGERS
============================================================================= */
GO

-- 11.1 Trigger for Purchase/Import Stock Update
CREATE TRIGGER trg_update_stock_import
ON purchase_order_details
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    MERGE inventory AS target
    USING (
        SELECT isbn, SUM(quantity) AS total_qty 
        FROM inserted 
        GROUP BY isbn
    ) AS source
    ON target.isbn = source.isbn
    WHEN MATCHED THEN
        UPDATE SET stock_quantity = target.stock_quantity + source.total_qty
    WHEN NOT MATCHED THEN
        INSERT (isbn, stock_quantity) VALUES (source.isbn, source.total_qty);
        
    -- Update Purchase Order total
    UPDATE po
    SET total_amount = (
        SELECT SUM(quantity * purchase_price) 
        FROM purchase_order_details 
        WHERE purchase_order_id = po.purchase_order_id
    )
    FROM purchase_orders po
    JOIN (SELECT DISTINCT purchase_order_id FROM inserted) i ON po.purchase_order_id = i.purchase_order_id;
END
GO

-- 11.2 Trigger for Export Stock Update
CREATE TRIGGER trg_update_stock_export
ON export_order_details
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE i
    SET stock_quantity = i.stock_quantity - e.total_qty
    FROM inventory i
    JOIN (
        SELECT isbn, SUM(quantity) AS total_qty
        FROM inserted
        GROUP BY isbn
    ) e ON i.isbn = e.isbn;

    IF EXISTS (SELECT 1 FROM inventory WHERE stock_quantity < 0)
    BEGIN
        THROW 50001, N'Stock Error: Insufficient inventory quantity!', 1;
    END
END
GO

-- 11.3 Trigger for Order Pricing Calculation
CREATE TRIGGER trg_calculate_order_totals
ON order_details
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @AffectedOrders TABLE (order_id NVARCHAR(20));
    
    INSERT INTO @AffectedOrders (order_id)
    SELECT order_id FROM inserted 
    UNION 
    SELECT order_id FROM deleted;

    UPDATE ord
    SET 
        total_items_price = ISNULL(t.total, 0),
        total_payment = CASE 
            WHEN (ISNULL(t.total, 0) + ISNULL(ord.shipping_fee, 0) - ISNULL(v.discount_value, 0)) < 0 
            THEN 0
            ELSE (ISNULL(t.total, 0) + ISNULL(ord.shipping_fee, 0) - ISNULL(v.discount_value, 0))
        END
    FROM orders ord
    LEFT JOIN (
        SELECT order_id, SUM(quantity * final_price) AS total
        FROM order_details
        GROUP BY order_id
    ) t ON ord.order_id = t.order_id
    LEFT JOIN vouchers v ON ord.voucher_code = v.voucher_code
    WHERE ord.order_id IN (SELECT order_id FROM @AffectedOrders);
END
GO
