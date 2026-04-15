package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class BookRankingDTO {
    @Schema(example = "9786041123456")
    private String isbn;

    @Schema(example = "The adventure of Cricket")
    private String title;

    @Schema(example = "125")
    private long totalSold;

    public BookRankingDTO(String isbn, String title, long totalSold) {
        this.isbn = isbn;
        this.title = title;
        this.totalSold = totalSold;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public long getTotalSold() { return totalSold; }
}
