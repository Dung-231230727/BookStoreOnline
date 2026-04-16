package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.category c " +
           "LEFT JOIN b.publisher p " +
           "LEFT JOIN b.authors a " +
           "WHERE (:status IS NULL OR b.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR c.id = :categoryId) AND " +
           "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR b.price <= :maxPrice)")
    org.springframework.data.domain.Page<Book> searchAndFilterBooks(
                                    @Param("keyword") String keyword,
                                    @Param("categoryId") Long categoryId,
                                    @Param("status") com.bookstore.enums.BookStatus status,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    org.springframework.data.domain.Pageable pageable);
}
