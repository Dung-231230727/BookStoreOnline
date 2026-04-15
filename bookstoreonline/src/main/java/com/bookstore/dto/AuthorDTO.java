package com.bookstore.dto;

public class AuthorDTO {
    private Integer authorId;
    private String authorName;
    private String biography;

    public AuthorDTO() {}

    public AuthorDTO(Integer authorId, String authorName, String biography) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.biography = biography;
    }

    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}
