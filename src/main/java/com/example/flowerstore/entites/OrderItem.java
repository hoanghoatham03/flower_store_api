package com.example.flowerstore.entites;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Integer quantity;
    private Double oderPrice;

    @ManyToOne
    @JoinColumn(name = "orderId")
    @JsonBackReference("order-items")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonIgnoreProperties({"orderItems", "cartItems", "comments", "images"})
    private Product product;
}
