package com.example.flowerstore.controllers;
    
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flowerstore.dto.request.CategoryDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.services.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;

    // Get all categories for user
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Object>> getAllCategories() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Categories fetched successfully", categoryService.getAllCategories()));
    }

    // Get category by id for user
    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Object>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category fetched successfully", categoryService.getCategoryById(id)));
    }

    // Create category for admin
    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<Object>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "Category created successfully", categoryService.createCategory(categoryDTO)));
    }

    // Update category for admin
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category updated successfully", categoryService.updateCategory(id, categoryDTO)));
    }

    // Delete category for admin
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<Void>(HttpStatus.OK.value(), "Category deleted successfully", null));
    }
}
