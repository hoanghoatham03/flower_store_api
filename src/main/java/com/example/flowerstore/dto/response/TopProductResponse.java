package com.example.flowerstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopProductResponse {
    private Long productId;
    private String productName;
    private Integer totalPurchased;
    private Double price;
} 