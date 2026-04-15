package com.bookstore.constant;

public enum AuditAction {
    // Authentication
    LOGIN,
    LOGOUT,
    CHANGE_PASSWORD,
    REGISTER,

    // Profile
    UPDATE_PROFILE,
    CREATE_PROFILE,

    // Inventory
    STOCK_UPDATE,
    IMPORT_BOOKS,
    EXPORT_BOOKS,

    // Orders
    CREATE_ORDER,
    UPDATE_ORDER_STATUS,
    CANCEL_ORDER,

    // Management
    CREATE_BOOK,
    UPDATE_BOOK,
    DELETE_BOOK,
    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,
    UPDATE_VOUCHER,
    
    // Support
    CREATE_TICKET,
    CLOSE_TICKET
}
