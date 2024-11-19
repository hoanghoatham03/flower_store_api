package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.CartDTO;
import com.example.flowerstore.entites.Cart;

public interface CartService {
    Cart createCart(Long userId, CartDTO cartDTO);
    Cart getCartByUserId(Long userId);
    Cart updateCart(Long userId, CartDTO cartDTO);
    void deleteCart(Long userId);
    void deleteProductFromCart(Long userId, Long productId);
}
