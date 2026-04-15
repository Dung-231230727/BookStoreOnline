package com.bookstore.repository;

import com.bookstore.entity.SupportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportStatusRepository extends JpaRepository<SupportStatus, String> {
}
