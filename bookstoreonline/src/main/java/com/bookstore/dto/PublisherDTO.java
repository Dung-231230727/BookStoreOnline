package com.bookstore.dto;

public class PublisherDTO {
    private Integer publisherId;
    private String publisherName;

    public PublisherDTO() {}

    public PublisherDTO(Integer publisherId, String publisherName) {
        this.publisherId = publisherId;
        this.publisherName = publisherName;
    }

    public Integer getPublisherId() { return publisherId; }
    public void setPublisherId(Integer publisherId) { this.publisherId = publisherId; }
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
}
