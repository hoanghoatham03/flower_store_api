package com.example.flowerstore.services;

import com.example.flowerstore.entites.Payment;
import com.example.flowerstore.dto.request.PaymentDTO;

public interface PaymentService {
    Payment getPaymentById(Long id);
    Payment createPayment(PaymentDTO paymentDTO);
} 