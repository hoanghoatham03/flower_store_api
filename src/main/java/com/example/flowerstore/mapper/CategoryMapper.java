package com.example.flowerstore.mapper;

import com.example.flowerstore.dto.request.CategoryDTO;
import com.example.flowerstore.entites.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

}
