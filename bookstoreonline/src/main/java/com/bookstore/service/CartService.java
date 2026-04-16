package com.bookstore.service;

import com.bookstore.dto.CartDTO;
import com.bookstore.dto.CartAdminResponseDTO;
import com.bookstore.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CartService {
    List<CartDTO> getCart(String username);
    void addToCart(String username, String isbn, Integer quantity);
    void updateQuantity(String username, String isbn, Integer quantity);
    void removeFromCart(String username, String isbn);
    void clearCart(String username);
    List<Cart> getAllActiveCarts();
    Page<CartAdminResponseDTO> getAllActiveCartsDTO(Pageable pageable);
}
