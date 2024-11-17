package com.example.flowerstore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productName;
    private String description;
    private Integer stock;
    private Double price;
    private Double discount;
    private Long categoryId;
    private List<MultipartFile> images;
} 