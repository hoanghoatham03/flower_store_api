package com.example.flowerstore.entites;

import java.time.LocalDateTime;

import com.example.flowerstore.util.AppConstant;
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

}



