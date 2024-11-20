package com.example.flowerstore.dto.request;

import lombok.Data;

@Data
public class OrderDTO {
    private Long userId;
    private Long addressId;
    private Long paymentId;
}
