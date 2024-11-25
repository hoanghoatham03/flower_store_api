package com.example.flowerstore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flowerstore.dto.request.CartDTO;
import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.entites.Cart;
import com.example.flowerstore.entites.CartItem;
import com.example.flowerstore.entites.Product;
import com.example.flowerstore.mapper.ProductDetailResponseMapper;
import com.example.flowerstore.repositories.CartRepository;
import com.example.flowerstore.util.AppConstant;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;
    @Autowired
    private ProductDetailResponseMapper productMapper;
    private final int MAX_QUANTITY_PER_ITEM = AppConstant.MAX_QUANTITY_PER_ITEM;

    private Cart getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(userService.getUserProfile(userId));
            cart.setCartItems(new ArrayList<>());
            cart.setTotalPrice(0.0);
        }
        return cart;
    }

    private void validateCartItem(CartItem item) {
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (item.getQuantity() > MAX_QUANTITY_PER_ITEM) {
            throw new IllegalArgumentException("Quantity exceeds maximum allowed");
        }
    }

    private void validateCartItems(List<CartItem> items) {
        for (CartItem item : items) {
            validateCartItem(item);
        }
    }

    private void recalculateCartTotal(Cart cart) {
        validateCartItems(cart.getCartItems());
        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
    }

    //add product to cart
    @Override
    public Cart createCart(Long userId, CartDTO cartDTO) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(cartDTO.getProductId()))
                .findFirst()
                .orElse(null);

        ProductDetailResponse productResponse = productService.getProductById(cartDTO.getProductId());
        Product product = productMapper.toEntity(productResponse);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(cartDTO.getQuantity());
            cartItem.setPrice(product.getRealPrice());
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + cartDTO.getQuantity());
        }

        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    //update cart
    @Override
    public Cart updateCart(Long userId, CartDTO cartDTO) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(cartDTO.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        cartItem.setQuantity(cartDTO.getQuantity());
        
        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    //get cart by user id
    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUser_UserId(userId);
    }

    //delete cart
    @Override
    @Transactional
    public void deleteCart(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        if (cart != null) {
            cartRepository.delete(cart);
        }
    }

    //delete product from cart
    @Override
    @Transactional
    public void deleteProductFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getProduct().getProductId().equals(productId));
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }
}
