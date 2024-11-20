package com.example.flowerstore.dto.request;

import com.example.flowerstore.util.AppConstant.PaymentMethod;
import lombok.Data;

@Data
public class PaymentDTO {
    private PaymentMethod paymentMethod;
} 