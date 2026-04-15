package com.bookstore.repository;

import com.bookstore.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    
    Optional<Staff> findByAccount_Username(String username);

    List<Staff> findByAccount_UsernameIn(Collection<String> usernames);
}
