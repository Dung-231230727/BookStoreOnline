package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiSearchService {

    private final BookService bookService;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public AiSearchService(BookService bookService, 
                          CategoryRepository categoryRepository,
                          PublisherRepository publisherRepository) {
        this.bookService = bookService;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    public List<BookDTO> searchByNaturalLanguage(String query) {
        if (query == null || query.isBlank()) {
            return bookService.searchAndFilterBooks(null, null, null, null, null);
        }

        String lowerQuery = query.toLowerCase();

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        String categoryName = null;
        String keyword = null;

        // 1. Extract category from DB and basic semantic mapping
        List<String> possibleCategories = categoryRepository.findAll().stream()
                .map(cat -> cat.getCategoryName().toLowerCase())
                .filter(cat -> cat.length() > 2)
                .toList();

        // Basic semantic mapping (Simple mechanism)
        if (lowerQuery.contains("startup") || lowerQuery.contains("business") || lowerQuery.contains("rich")) {
            categoryName = "economics"; 
            lowerQuery = lowerQuery.replace("startup", "").replace("business", "").replace("rich", "");
        }

        if (categoryName == null) {
            for (String cat : possibleCategories) {
                if (lowerQuery.matches(".*\\b" + Pattern.quote(cat) + "\\b.*")) {
                    categoryName = cat;
                    lowerQuery = lowerQuery.replaceFirst("\\b" + Pattern.quote(cat) + "\\b", "");
                    break;
                }
            }
        }

        // 2. Extract price ranges (Vietnamese & English keywords)
        Pattern maxPricePattern = Pattern
                .compile("(?:under|below|smaller|cheaper|max|dưới|rẻ hơn|tối đa)\\s*(\\d+)\\s*(k|thousand|nghìn|000|đ|vnd|vnđ)?");
        Matcher maxMatcher = maxPricePattern.matcher(lowerQuery);
        if (maxMatcher.find()) {
            long number = Long.parseLong(maxMatcher.group(1));
            String unit = maxMatcher.group(2);
            if (unit != null && (unit.equals("k") || unit.equals("nghìn") || unit.equals("thousand") || unit.equals("000"))) {
                number = number * 1000;
            } else if (number < 1000 && number > 0) { 
                number = number * 1000;
            }
            maxPrice = BigDecimal.valueOf(number);
            lowerQuery = lowerQuery.replace(maxMatcher.group(0), ""); 
        }

        Pattern minPricePattern = Pattern
                .compile("(?:above|over|from|min|at least|trên|lớn hơn|từ|tối thiểu)\\s*(\\d+)\\s*(k|thousand|nghìn|000|đ|vnd|vnđ)?");
        Matcher minMatcher = minPricePattern.matcher(lowerQuery);
        if (minMatcher.find()) {
            long number = Long.parseLong(minMatcher.group(1));
            String unit = minMatcher.group(2);
            if (unit != null && (unit.equals("k") || unit.equals("nghìn") || unit.equals("thousand") || unit.equals("000"))) {
                number = number * 1000;
            } else if (number < 1000 && number > 0) {
                number = number * 1000;
            }
            minPrice = BigDecimal.valueOf(number);
            lowerQuery = lowerQuery.replace(minMatcher.group(0), "");
        }

        // 3. Extract Publisher from DB
        List<String> possiblePublishers = publisherRepository.findAll().stream()
                .map(p -> p.getPublisherName().toLowerCase())
                .toList();
        String publisherName = null;
        for (String pub : possiblePublishers) {
            if (lowerQuery.contains(pub)) {
                publisherName = pub;
                lowerQuery = lowerQuery.replace(pub, "");
                break;
            }
        }

        // 4. Stopwords removal
        if (lowerQuery.trim().split("\\s+").length > 3) {
            String[] stopWords = { "find", "search", "show", "me", "want", "to", "buy", "with", "about", "topic",
                                   "books", "book", "category", "price", "around", "money", "from", "publisher", 
                                   "nxb", "nhà", "xuất", "bản", "quyển", "cuốn", "thể", "loại" };
            for (String word : stopWords) {
                lowerQuery = lowerQuery.replaceAll("(?U)\\b" + word + "\\b", " "); 
            }
        }

        // Clean extra spaces
        keyword = lowerQuery.replaceAll("\\s+", " ").trim();
        if (keyword.length() < 2) {
            keyword = null;
        }

        return bookService.searchAndFilterBooks(keyword, categoryName, publisherName, minPrice, maxPrice);
    }
}
