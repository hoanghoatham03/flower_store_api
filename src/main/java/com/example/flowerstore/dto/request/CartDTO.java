package com.example.flowerstore.dto.request;

import lombok.Data;

@Data
public class CartDTO {
    private Long productId;
    private Integer quantity;
}
