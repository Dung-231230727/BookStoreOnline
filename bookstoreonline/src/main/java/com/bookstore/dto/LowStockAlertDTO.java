package com.bookstore.dto;

public class LowStockAlertDTO {
    private String isbn;
    private String title;
    private int stockQuantity;
    private int alertThreshold;

    public LowStockAlertDTO() {}

    public LowStockAlertDTO(String isbn, String title, int stockQuantity, int alertThreshold) {
        this.isbn = isbn;
        this.title = title;
        this.stockQuantity = stockQuantity;
        this.alertThreshold = alertThreshold;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }
}