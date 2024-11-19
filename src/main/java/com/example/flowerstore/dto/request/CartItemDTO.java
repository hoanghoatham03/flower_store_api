package com.example.flowerstore.dto.request;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private Double price;

}
