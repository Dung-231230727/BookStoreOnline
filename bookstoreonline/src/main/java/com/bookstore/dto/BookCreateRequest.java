package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

public class BookCreateRequest {

    @NotBlank(message = "ISBN cannot be empty")
    private String isbn;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Price cannot be empty")
    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Category ID cannot be empty")
    private Integer categoryId;

    @NotNull(message = "Publisher ID cannot be empty")
    private Integer publisherId;

    private String description;

    private String coverImage;

    private Set<Integer> authorIds;

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getPublisherId() { return publisherId; }
    public void setPublisherId(Integer publisherId) { this.publisherId = publisherId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Set<Integer> getAuthorIds() { return authorIds; }
    public void setAuthorIds(Set<Integer> authorIds) { this.authorIds = authorIds; }
}
