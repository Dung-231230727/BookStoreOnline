package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ebooks")
@PrimaryKeyJoinColumn(name = "isbn")
public class EBook extends Book {
    @Column(name = "file_size", precision = 10, scale = 2)
    private BigDecimal fileSize;

    @Column(name = "download_url", length = 255)
    private String downloadUrl;

    public EBook() {}

    public BigDecimal getFileSize() { return fileSize; }
    public void setFileSize(BigDecimal fileSize) { this.fileSize = fileSize; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
