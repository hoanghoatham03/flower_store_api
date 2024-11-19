package com.example.flowerstore.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flowerstore.dto.request.CartDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.entites.Cart;
import com.example.flowerstore.services.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;

    //add product to cart
    @PostMapping("/carts/{userId}")
    public ResponseEntity<ApiResponse<Cart>> createCart(@PathVariable Long userId, @RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart created successfully", cartService.createCart(userId, cartDTO)));
    }

    //get cart by user id
    @GetMapping("/carts/{userId}")
    public ResponseEntity<ApiResponse<Cart>> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart retrieved successfully", cartService.getCartByUserId(userId)));
    }

    //update cart
    @PutMapping("/carts/{userId}")
    public ResponseEntity<ApiResponse<Cart>> updateCart(@PathVariable Long userId, @RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart updated successfully", cartService.updateCart(userId, cartDTO)));
    }

    //delete cart
    @DeleteMapping("/carts/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable Long userId) {
        cartService.deleteCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cart deleted successfully", null));
    }

    //delete product from cart
    @DeleteMapping("/carts/{userId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        cartService.deleteProductFromCart(userId, productId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Product deleted from cart successfully", null));
    }
}
