package com.bookstore.dto;

import java.util.List;

public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private Integer parentId;
    private List<CategoryDTO> subCategories;

    public CategoryDTO() {}

    public CategoryDTO(Integer categoryId, String categoryName, Integer parentId, List<CategoryDTO> subCategories) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.parentId = parentId;
        this.subCategories = subCategories;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public List<CategoryDTO> getSubCategories() { return subCategories; }
    public void setSubCategories(List<CategoryDTO> subCategories) { this.subCategories = subCategories; }
}
