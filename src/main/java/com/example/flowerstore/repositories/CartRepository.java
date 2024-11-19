package com.example.flowerstore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flowerstore.entites.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser_UserId(Long userId);
    void deleteByUser_UserId(Long userId);
}
