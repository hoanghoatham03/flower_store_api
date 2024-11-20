package com.example.flowerstore.services;

import org.springframework.stereotype.Service;
import com.example.flowerstore.entites.Payment;
import com.example.flowerstore.dto.request.PaymentDTO;
import com.example.flowerstore.repositories.PaymentRepository;
import com.example.flowerstore.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));
    }

    @Override
    public Payment createPayment(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        return paymentRepository.save(payment);
    }
} 