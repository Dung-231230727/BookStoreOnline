package com.bookstore.service;

import com.bookstore.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    List<ReviewDTO> getReviewsByBook(String isbn);
    void submitReview(String username, String isbn, Integer rating, String comment);
    void deleteReview(Long reviewId);
}
