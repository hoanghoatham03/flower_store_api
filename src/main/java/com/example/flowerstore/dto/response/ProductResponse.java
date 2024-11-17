package com.example.flowerstore.dto.response;

import lombok.Data;

@Data
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private Integer stock;
    private Double price;
    private Double discount;
    private Double realPrice;
    private Long categoryId;
    private String imageUrl;
} 