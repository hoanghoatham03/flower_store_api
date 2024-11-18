package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.ProductDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.dto.response.ProductResponse;
import com.example.flowerstore.entites.Product;
import com.example.flowerstore.services.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // Create a new product for admin
    @PostMapping(value = "/admin/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Product>> createProduct(@ModelAttribute ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO);
        
        ApiResponse<Product> response = new ApiResponse<>(
            HttpStatus.CREATED.value(),
            "Product created successfully",
            product
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update product by id for admin
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO);

        ApiResponse<Product> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Product updated successfully",
            product
        );
        return ResponseEntity.ok(response);
    }

    // Get all products for user
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Object>> getAllProducts(@ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        List<ProductResponse> products = productService.getAllProducts(pageable);

        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Get all products successfully",
            products
        );
        return ResponseEntity.ok(response);
    }

    // Get product by id for user
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long id) {
        ProductDetailResponse productDetailResponse = productService.getProductById(id);

        ApiResponse<ProductDetailResponse> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Get product by id successfully",
            productDetailResponse
        );
        return ResponseEntity.ok(response);
    }

    
}
