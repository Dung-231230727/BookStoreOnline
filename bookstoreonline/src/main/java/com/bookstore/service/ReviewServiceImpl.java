package com.bookstore.service;

import com.bookstore.dto.ReviewDTO;
import com.bookstore.entity.Review;
import com.bookstore.entity.Customer;
import com.bookstore.entity.Book;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, 
                              CustomerRepository customerRepository, 
                              BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsByBook(String isbn) {
        return reviewRepository.findByBook_Isbn(isbn).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submitReview(String username, String isbn, Integer rating, String comment) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Logged in customer not found"));
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new RuntimeException("Book does not exist"));

        Review review = new Review();
        review.setCustomer(customer);
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setIsbn(review.getBook().getIsbn());
        dto.setCustomerName(review.getCustomer().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getReviewDate());
        return dto;
    }
}
