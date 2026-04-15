package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.repository.BookRepository;
import com.bookstore.dto.BookCreateRequest;
import com.bookstore.dto.BookUpdateRequest;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.repository.PublisherRepository;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.Publisher;
import com.bookstore.entity.Author;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<BookDTO> searchAndFilterBooks(String keyword, String categoryName, String publisherName,
            BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.searchAndFilterBooks(keyword, categoryName, publisherName, minPrice, maxPrice)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));
        
        if (Boolean.TRUE.equals(book.getIsDeleted())) {
             throw new IllegalArgumentException("This book has been deleted from the system");
        }
        return BookDTO.fromEntity(book);
    }

    @Transactional
    public BookDTO createBook(BookCreateRequest request) {
        if (bookRepository.existsById(request.getIsbn())) {
            throw new IllegalArgumentException("Book with this ISBN already exists: " + request.getIsbn());
        }

        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setCoverImage(request.getCoverImage());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with ID: " + request.getCategoryId()));
        book.setCategory(category);

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Publisher not found with ID: " + request.getPublisherId()));
        book.setPublisher(publisher);

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
            if (authors.size() != request.getAuthorIds().size()) {
                throw new IllegalArgumentException("One or more author IDs are invalid");
            }
            book.setAuthors(new java.util.HashSet<>(authors));
        }

        return BookDTO.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public BookDTO updateBook(String isbn, BookUpdateRequest request) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));

        if (Boolean.TRUE.equals(book.getIsDeleted())) {
            throw new IllegalArgumentException("This book has been deleted and cannot be modified");
        }

        book.setTitle(request.getTitle());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setCoverImage(request.getCoverImage());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with ID: " + request.getCategoryId()));
        book.setCategory(category);

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Publisher not found with ID: " + request.getPublisherId()));
        book.setPublisher(publisher);

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
            if (authors.size() != request.getAuthorIds().size()) {
                throw new IllegalArgumentException("One or more author IDs are invalid");
            }
            book.setAuthors(new java.util.HashSet<>(authors));
        } else {
            book.setAuthors(new java.util.HashSet<>());
        }

        return BookDTO.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public void softDeleteBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));
        
        if (Boolean.TRUE.equals(book.getIsDeleted())) {
             throw new IllegalArgumentException("This book was already deleted");
        }
        
        book.setIsDeleted(true);
        bookRepository.save(book);
    }
}
