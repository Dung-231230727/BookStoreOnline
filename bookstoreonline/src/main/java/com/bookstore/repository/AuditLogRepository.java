package com.bookstore.repository;

import com.bookstore.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // BUG-8 fix: JOIN FETCH to avoid LazyInitializationException on account
    @Query("SELECT l FROM AuditLog l JOIN FETCH l.account ORDER BY l.timestamp DESC")
    List<AuditLog> findAllByOrderByTimestampDesc();

    // BUG-14 fix: Count by action type at DB level (avoid load-all antipattern)
    long countByActionIgnoreCase(String action);
}
