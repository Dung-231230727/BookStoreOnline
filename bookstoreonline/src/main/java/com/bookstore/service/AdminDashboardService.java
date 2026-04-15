package com.bookstore.service;

import com.bookstore.dto.AuditLogDTO;
import com.bookstore.dto.BookRankingDTO;
import com.bookstore.dto.RevenueReportDTO;
import com.bookstore.entity.Book;
import com.bookstore.repository.AuditLogRepository;
import com.bookstore.repository.OrderDetailRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AuditLogRepository auditLogRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;

    public AdminDashboardService(OrderRepository orderRepository,
                                 OrderDetailRepository orderDetailRepository,
                                 AuditLogRepository auditLogRepository,
                                 BookRepository bookRepository,
                                 CustomerRepository customerRepository,
                                 InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.auditLogRepository = auditLogRepository;
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getQuickStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", customerRepository.count());
        stats.put("totalBooks", bookRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatusCode("NEW"));
        stats.put("lowStockCount", inventoryRepository.findAll().stream().filter(k -> k.getStockQuantity() < 5).count());
        stats.put("totalRevenue", orderRepository.sumTotalAmountByStatusCode("COMPLETED"));
        return stats;
    }

    @Transactional(readOnly = true)
    public RevenueReportDTO getRevenueReport() {
        BigDecimal revenue = orderRepository.sumTotalAmountByStatusCode("COMPLETED");
        long count = orderRepository.countByStatusCode("COMPLETED");
        return new RevenueReportDTO(revenue, count, "COMPLETED");
    }

    @Transactional(readOnly = true)
    public List<BookRankingDTO> getBookRanking() {
        List<Object[]> results = orderDetailRepository.findTopSellingProjected();
        
        return results.stream().map(row -> {
            String isbn = (row[0] != null) ? (String) row[0] : "";
            long totalSold = (row[1] != null) ? (long) row[1] : 0L;
            
            String title = "";
            if (!isbn.isEmpty()) {
                title = bookRepository.findById(isbn)
                    .map(Book::getTitle)
                    .orElse("Unknown Book (Not found)");
            }
            
            return new BookRankingDTO(isbn, title, totalSold);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(AuditLogDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuditLogDTO getAuditLogDetail(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        return auditLogRepository.findById(id)
                .map(AuditLogDTO::new)
                .orElseThrow(() -> new RuntimeException("Audit log not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Object getAuditStats() {
        long totalLogs = auditLogRepository.count();
        long loginCount = auditLogRepository.findAll().stream()
                .filter(log -> "LOGIN".equalsIgnoreCase(log.getAction()))
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", totalLogs);
        stats.put("loginCount", loginCount);
        stats.put("lastScan", LocalDateTime.now());
        return stats;
    }
}
