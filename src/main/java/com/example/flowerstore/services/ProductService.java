package com.example.flowerstore.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.flowerstore.dto.request.ProductDTO;
import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.dto.response.ProductResponse;
import com.example.flowerstore.entites.Product;

public interface ProductService {
    Product createProduct(ProductDTO productDTO);
    List<ProductResponse> getAllProducts(Pageable pageable);
    List<ProductResponse> getProductsByCategoryId(Long categoryId, Pageable pageable);
    ProductDetailResponse getProductById(Long id);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
} 