package com.bookstore.repository;

import com.bookstore.entity.Cart;
import com.bookstore.entity.CartId;
import com.bookstore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {
    
    List<Cart> findByCustomer_Account_Username(String username);
    
    void deleteByCustomer_Account_Username(String username);
    
    Optional<Cart> findByCustomer_Account_UsernameAndBook_Isbn(String username, String isbn);

    List<Cart> findByCustomer(Customer customer);
}
