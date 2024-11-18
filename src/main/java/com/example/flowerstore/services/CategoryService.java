package com.example.flowerstore.services;

import java.util.List;

import com.example.flowerstore.dto.request.CategoryDTO;
import com.example.flowerstore.entites.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(CategoryDTO categoryDTO);
    Category updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
