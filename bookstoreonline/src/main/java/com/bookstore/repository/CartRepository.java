package com.bookstore.repository;

import com.bookstore.entity.Cart;
import com.bookstore.entity.CartId;
import com.bookstore.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.bookstore.dto.CartAdminResponseDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {
    
    List<Cart> findByCustomer_Account_Username(String username);
    
    void deleteByCustomer_Account_Username(String username);
    
    Optional<Cart> findByCustomer_Account_UsernameAndBook_Isbn(String username, String isbn);

    List<Cart> findByCustomer(Customer customer);

    @Query("SELECT new com.bookstore.dto.CartAdminResponseDTO(c.customer.account.username, c.customer.customerId, c.book.title, c.book.isbn, c.quantity, c.book.price) " +
           "FROM Cart c")
    Page<CartAdminResponseDTO> findAllAdminDTO(Pageable pageable);
}
