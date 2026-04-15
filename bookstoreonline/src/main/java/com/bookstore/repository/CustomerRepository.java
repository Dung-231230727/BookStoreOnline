package com.bookstore.repository;

import com.bookstore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByAccount_Username(String username);

    List<Customer> findByAccount_UsernameIn(Collection<String> usernames);
}
