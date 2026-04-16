package com.bookstore.service;

import com.bookstore.dto.ReviewDTO;
import java.security.Principal;

public interface ReviewService {
    org.springframework.data.domain.Page<ReviewDTO> getAllReviews(org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<ReviewDTO> getReviewsByBook(String isbn, org.springframework.data.domain.Pageable pageable);
    void submitReview(Principal principal, String isbn, Integer rating, String comment);
    void deleteReview(Long reviewId);
}
