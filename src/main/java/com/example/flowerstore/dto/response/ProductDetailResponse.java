package com.example.flowerstore.dto.response;

import com.example.flowerstore.entites.Comment;
import com.example.flowerstore.entites.ProductImage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailResponse {
    private Long productId;
    private String productName;
    private String description;
    private Integer stock;
    private Double price;
    private Double discount;
    private Double realPrice;
    private Long categoryId;
    private String categoryName;
    private List<Comment> comments;

    @JsonIgnoreProperties("product")
    private List<ProductImage> images;
} 