package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.ProductDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.dto.response.ProductPageResponse;
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
    @PutMapping(value = "/admin/products/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO);

        ApiResponse<Product> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Product updated successfully",
            product
        );
        return ResponseEntity.ok(response);
    }

    // Delete product by id for admin
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Product deleted successfully", null));
    }

    // Get all products for user
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getAllProducts(@ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        ProductPageResponse productPageResponse = productService.getAllProducts(pageable);

        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Get all products successfully",
            productPageResponse
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

    // Get products by category id for user
    @GetMapping("/products/categories/{categoryId}")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        ProductPageResponse productPageResponse = productService.getProductsByCategoryId(categoryId, pageable);

        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Get products by category successfully",
            productPageResponse
        );
        return ResponseEntity.ok(response);
    }

    // Get products by name for user
    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProductsByName(@RequestParam String name, @ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        ProductPageResponse productPageResponse = productService.getProductsByName(name, pageable);

        ApiResponse<ProductPageResponse> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Get products by name successfully",
            productPageResponse
        );
        return ResponseEntity.ok(response);
    }

    
}

