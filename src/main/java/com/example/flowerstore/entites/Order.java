package com.example.flowerstore.entites;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.flowerstore.util.AppConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;


    private LocalDateTime orderDate;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private AppConstant.OrderStatus orderStatus;


    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties({"cartItems", "orderItems", "comments", "addresses"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("order-items")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AppConstant.PaymentStatus paymentStatus;

    @PrePersist
    public void prePersist() {
        this.orderDate = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

}



