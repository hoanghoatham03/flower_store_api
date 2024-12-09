package com.example.flowerstore.entites;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cartId")
    @JsonBackReference("cart-items")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonIgnoreProperties({"cartItems", "orderItems", "comments"})
    private Product product;

    private Integer quantity;
    private Double price;
}
