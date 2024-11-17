package com.example.flowerstore.mapper;

import com.example.flowerstore.dto.response.ProductResponse;
import com.example.flowerstore.entites.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "imageUrl", expression = "java(product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl())")
    ProductResponse toProductResponse(Product product);
} 