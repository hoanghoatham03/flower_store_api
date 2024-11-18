package com.example.flowerstore.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flowerstore.dto.request.CategoryDTO;
import com.example.flowerstore.entites.Category;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.CategoryMapper;
import com.example.flowerstore.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // Get all categories for user
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get category by id for user
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category =" + id));
    }

    // Create category for admin
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        return categoryRepository.save(categoryMapper.toEntity(categoryDTO));
    }

    // Update category for admin
    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = getCategoryById(id);
        
        if (categoryDTO.getCategoryName() != null && !categoryDTO.getCategoryName().isEmpty()) {
            category.setCategoryName(categoryDTO.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    // Delete category for admin
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
