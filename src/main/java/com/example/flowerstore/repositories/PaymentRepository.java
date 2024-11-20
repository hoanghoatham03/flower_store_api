package com.example.flowerstore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.flowerstore.entites.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
} 